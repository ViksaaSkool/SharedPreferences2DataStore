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
import timber.log.Timber
import javax.inject.Inject
import kotlin.Boolean
import kotlin.concurrent.timer

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

    private val combinedDataFlow = combine(
        sp2DataStore.userFlow,
        sp2DataStore.threeWordDescriptionFlow,
        flowOf(sp2DataStore.androidRate)
    ) { user, description, rate ->
        DataScreenState(
            user = user,
            description = description,
            androidRate = rate,
            isInitialized = true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = DataScreenState()
    )

    init {
        // Collect combinedDataFlow and update canSave and canClear reactively
        viewModelScope.launch {
            combinedDataFlow.collect { baseState ->
                val canSave = calculateCanSave(baseState)
                val canClear = baseState.hasDataChangedFromDefault()
                _dataScreenStateFlow.emit(
                    baseState.copy(canSave = canSave, canClear = canClear)
                )
            }
        }
    }


    private fun updateState(update: DataScreenState.() -> DataScreenState) {
        val current = _dataScreenStateFlow.value
        val updatedBase = current.update()

        viewModelScope.launch {
            _dataScreenStateFlow.emit(
                updatedBase
            )
        }
        updateButtons(updatedBase)

    }

    private fun updateButtons(state: DataScreenState) = viewModelScope.launch {
        val buttonsState = _dataScreenStateFlow.value.copy(
            isInitialized = true,
            canSave = calculateCanSave(state.copy()),
            canClear = state.copy().hasDataChangedFromDefault()
        )
        _dataScreenStateFlow.emit(buttonsState)
    }

    override fun onImageUriChanged(uri: Uri) = updateState {
        copy(user = user.toBuilder().setPicUri(uri.toString()).build())
    }

    override fun onFirstNameChanged(firstName: String) = updateState {
        copy(user = user.toBuilder().setFirstName(firstName).build())
    }

    override fun onLastNameChanged(lastName: String) = updateState {
        copy(user = user.toBuilder().setLastName(lastName).build())
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
        }
        updateState { DataScreenState() }
        //updateButtons(DataScreenState())
    }

    override fun onSaveData() {
        super.onSaveData()
        val current = _dataScreenStateFlow.value

        // Save current data into datastore; ideally expose proper setters instead of overwriting flows
        viewModelScope.launch {
            sp2DataStore.apply {
                userFlow = flowOf(current.user)
                threeWordDescriptionFlow = flowOf(current.description)
                androidRate = current.androidRate
            }
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