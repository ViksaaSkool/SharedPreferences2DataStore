package com.droidconlisbon.sp2ds.ui.composables.data

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidconlisbon.sp2ds.storage.datastore.Sp2DsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Boolean

abstract class IDataViewModel : ViewModel() {
    open fun clearData() = Unit
    open fun onSaveData() = Unit
    open val dataScreenStateFlow =
        MutableStateFlow(DataScreenState()).asStateFlow()

    open fun onFirstNameChanged(firstName: String) = Unit
    open fun onLastNameChanged(lastName: String) = Unit
    open fun onImageUriChanged(uri: Uri) = Unit
    open fun onDescriptionChanged(description: List<String>) = Unit
    open fun onRateChange(value: Float) = Unit
}


@HiltViewModel
class DataViewModel @Inject constructor(
    private val sp2DataStore: Sp2DsDataStore
) : IDataViewModel() {

    private val _dataScreenStateFlow = MutableStateFlow(DataScreenState())
    override val dataScreenStateFlow = _dataScreenStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            with(sp2DataStore) {
                val pair = combine(
                    sp2DataStore.userFlow,
                    sp2DataStore.threeWordDescriptionFlow,
                ) { user, description ->
                    Pair(user, description)
                }.first()
                val baseState = DataScreenState(
                    user = pair.first,
                    description = pair.second,
                    androidRate = androidRate
                )
                val canSave = calculateCanSave(baseState)
                val canClear = baseState.hasDataChangedFromDefault()
                _dataScreenStateFlow.emit(
                    _dataScreenStateFlow.value.copy(
                        user = pair.first,
                        description = pair.second,
                        androidRate = androidRate,
                        isInitialized = true,
                        canSave = canSave,
                        canClear = canClear
                    )
                )
            }
        }
    }


    private fun updateState(update: DataScreenState.() -> DataScreenState) {
        val current = _dataScreenStateFlow.value
        val updatedBase = current.update()

        viewModelScope.launch {
            _dataScreenStateFlow.emit(updatedBase)
        }
        updateButtons(updatedBase)

    }

    private fun updateButtons(state: DataScreenState) = viewModelScope.launch {
        val buttonsState = _dataScreenStateFlow.value.copy(
            isInitialized = true,
            canSave = calculateCanSave(state.copy()),
            canClear = state.copy().hasDataChangedFromDefault()
        )
        _dataScreenStateFlow.value = buttonsState
    }

    override fun onImageUriChanged(uri: Uri) = updateState {
        copy(user = user.copy(picUri = uri.toString()))
    }

    override fun onFirstNameChanged(firstName: String) = updateState {
        copy(user = user.copy(firstName = firstName))
    }

    override fun onLastNameChanged(lastName: String) = updateState {
        copy(user = user.copy(lastName = lastName))
    }

    override fun onRateChange(value: Float) = updateState {
        copy(androidRate = value)
    }

    override fun onDescriptionChanged(description: List<String>) = updateState {
        copy(description = description)
    }

    override fun clearData() {
        viewModelScope.launch {
            sp2DataStore.clearData()
            _dataScreenStateFlow.emit(DataScreenState().copy(isInitialized = true))
        }
    }

    override fun onSaveData() {
        super.onSaveData()
        val current = _dataScreenStateFlow.value.copy()
        sp2DataStore.run {
            userFlow = flowOf(current.user)
            threeWordDescriptionFlow = flowOf(current.description)
            androidRate = current.androidRate
        }
    }

    private suspend fun calculateCanSave(state: DataScreenState): Boolean {
        val pair = combine(
            sp2DataStore.userFlow,
            sp2DataStore.threeWordDescriptionFlow
        ) { user, desc -> Pair(user, desc) }
            .first()

        return state.hasDataBeenChanged(
            u = pair.first,
            desc = pair.second,
            rate = sp2DataStore.androidRate
        ) && state.isDataValid()
    }
}