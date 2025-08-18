package com.droidconlisbon.sp2ds.ui.composables.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidconlisbon.sp2ds.model.ChatMessage
import com.droidconlisbon.sp2ds.model.MessageType
import com.droidconlisbon.sp2ds.network.ChatService
import com.droidconlisbon.sp2ds.network.data.ChatResponse
import com.droidconlisbon.sp2ds.network.data.ResultWrapper
import com.droidconlisbon.sp2ds.storage.sharedpreferences.Sp2DsSharedPreferencesManager
import com.droidconlisbon.sp2ds.util.hasMinutesPassed
import com.droidconlisbon.sp2ds.util.toFormattedDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


abstract class IHomeViewModel : ViewModel() {
    open fun onChat(query: String) = Unit
    open fun onTermsAccepted() = Unit
    open val homeScreenDataStateFlow =
        MutableStateFlow(HomeScreenDataState()).asStateFlow()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    val chatService: ChatService,
    val sharedPreferencesManager: Sp2DsSharedPreferencesManager
) : IHomeViewModel() {

    private val _homeScreenDataStateFlow = MutableStateFlow(HomeScreenDataState())
    override val homeScreenDataStateFlow = _homeScreenDataStateFlow.asStateFlow()

    private var homeScreenDataStateJob: Job? = null
    private fun updateHomeDataStateState(homeDataStateStateFlow: HomeScreenDataState) {
        homeScreenDataStateJob?.cancel()
        homeScreenDataStateJob = viewModelScope.launch {
            _homeScreenDataStateFlow.emit(homeDataStateStateFlow)
        }
    }

    init {
        with(sharedPreferencesManager) {
            updateHomeDataStateState(
                HomeScreenDataState(
                    chatMessages = chatMessages.toMutableList(),
                    hasBeenOnboarded = hasBeenOnboarded,
                    timeOutTimestamp = timeoutTimestamp,
                    avatarUri = user.picUri,
                    hasPopulatedData = hasStoredValidData()
                )
            )
        }
    }

    override fun onChat(query: String) {
        if (!validateState(query)) {
            return
        }
        viewModelScope.launch {
            chatService.chat(query).collect { result ->
                when (result) {
                    is ResultWrapper.Success<ChatResponse> -> {
                        updateHomeDataStateState(
                            _homeScreenDataStateFlow.value.copy(
                                chatMessages = getUpdatedList(
                                    query = result.value.getAnswer(),
                                    type = MessageType.ANSWER
                                ),
                                isLoading = false
                            )
                        )

                    }

                    is ResultWrapper.Error -> {
                        updateHomeDataStateState(
                            _homeScreenDataStateFlow.value.copy(
                                errorMessage = result.value.error.message,
                                isLoading = false
                            )
                        )
                    }

                    is ResultWrapper.UnknownError -> {
                        updateHomeDataStateState(
                            _homeScreenDataStateFlow.value.copy(
                                errorMessage = result.exception.message ?: "",
                                isLoading = false
                            )
                        )
                    }

                    else -> {
                        updateHomeDataStateState(
                            _homeScreenDataStateFlow.value.copy(
                                isLoading = false
                            )
                        )
                    }
                }
            }
        }

    }

    override fun onTermsAccepted() {
        sharedPreferencesManager.hasBeenOnboarded = true
        updateHomeDataStateState(
            _homeScreenDataStateFlow.value.copy(
                hasBeenOnboarded = true
            )
        )
    }

    private fun getUpdatedList(query: String, type: MessageType): List<ChatMessage> {
        val updatedList = _homeScreenDataStateFlow.value.chatMessages +
                ChatMessage(message = query, messageType = type)
        sharedPreferencesManager.chatMessages = updatedList
        _homeScreenDataStateFlow.update { currentState ->
            currentState.copy(chatMessages = updatedList.toMutableList())
        }
        return updatedList
    }

    fun validateState(query: String): Boolean = with(sharedPreferencesManager) {
        if (questionsLeft == 0) {
            if (timeoutTimestamp.isEmpty()) {
                timeoutTimestamp = System.currentTimeMillis().toFormattedDate()
                updateHomeDataStateState(
                    _homeScreenDataStateFlow.value.copy(
                        timeOutTimestamp = timeoutTimestamp
                    )
                )
                return@with false

            } else if (timeoutTimestamp.hasMinutesPassed()) {
                questionsLeft = 4
                timeoutTimestamp = ""
                updateHomeDataStateState(
                    _homeScreenDataStateFlow.value.copy(
                        timeOutTimestamp = timeoutTimestamp,
                        isLoading = true,
                        chatMessages = getUpdatedList(query = query, type = MessageType.QUESTION)
                    )
                )
                return@with true
            } else {
                updateHomeDataStateState(
                    _homeScreenDataStateFlow.value.copy(
                        timeOutTimestamp = timeoutTimestamp
                    )
                )
                return@with false
            }
        } else {
            questionsLeft--
            updateHomeDataStateState(
                _homeScreenDataStateFlow.value.copy(
                    isLoading = true,
                    chatMessages = getUpdatedList(query = query, type = MessageType.QUESTION)
                )
            )
            return@with true
        }
    }

}