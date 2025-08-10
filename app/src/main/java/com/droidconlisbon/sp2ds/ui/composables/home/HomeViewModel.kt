package com.droidconlisbon.sp2ds.ui.composables.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidconlisbon.sp2ds.model.ChatMessage
import com.droidconlisbon.sp2ds.model.MessageType
import com.droidconlisbon.sp2ds.network.ChatService
import com.droidconlisbon.sp2ds.network.data.ChatResponse
import com.droidconlisbon.sp2ds.network.data.ResultWrapper
import com.droidconlisbon.sp2ds.storage.datastore.Sp2DsDataStore
import com.droidconlisbon.sp2ds.util.hasMinutesPassed
import com.droidconlisbon.sp2ds.util.toFormattedDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject


abstract class IHomeViewModel : ViewModel() {
    open fun onChat(query: String) = Unit
    open fun onTermsAccepted() = Unit
    open fun refreshData() = Unit
    open val homeScreenDataStateFlow =
        MutableStateFlow(HomeScreenDataState()).asStateFlow()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    val chatService: ChatService,
    val sp2DataStore: Sp2DsDataStore
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
        refreshData()
    }

    override fun refreshData() {
        viewModelScope.launch {
            with(sp2DataStore) {
                val triple = combine(
                    userFlow,
                    chatMessagesFlow,
                    isOnboardingShownFlow
                ) { user, chatMessages, isOnboardingShown ->
                    Triple(user, chatMessages, isOnboardingShown)
                }.first()
                updateHomeDataStateState(
                    _homeScreenDataStateFlow.value.copy(
                        chatMessages = triple.second.toMutableList(),
                        hasBeenOnboarded = triple.third,
                        timeOutTimestamp = timeoutTimestamp,
                        avatarUri = triple.first.picUri,
                        hasPopulatedData = hasStoredValidData(),
                        isInitialized = true
                    )
                )
            }
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
        sp2DataStore.isOnboardingShownFlow = flowOf(true)
        updateHomeDataStateState(
            _homeScreenDataStateFlow.value.copy(
                hasBeenOnboarded = true
            )
        )
    }

    private fun getUpdatedList(query: String, type: MessageType): MutableList<ChatMessage> {
        val resultList = _homeScreenDataStateFlow.value.copy().chatMessages
        resultList.add(ChatMessage(query, type))
        sp2DataStore.chatMessagesFlow = flowOf(resultList)
        return resultList
    }

    fun validateState(query: String): Boolean = with(sp2DataStore) {
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
                questionsLeft = 7
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