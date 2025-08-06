package com.droidconlisbon.sp2ds.ui.composables.data

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidconlisbon.sp2ds.storage.sharedpreferences.Sp2DsSharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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
class DataViewModel @Inject constructor(val sharedPreferencesManager: Sp2DsSharedPreferencesManager) :
    IDataViewModel() {

    private val _dataScreenStateFlow = MutableStateFlow(DataScreenState())
    override val dataScreenStateFlow = _dataScreenStateFlow.asStateFlow()

    private var dataScreenStateJob: Job? = null
    private fun updateDataScreenState(dataScreenStateFlow: DataScreenState) {
        dataScreenStateJob?.cancel()
        dataScreenStateJob = viewModelScope.launch {
            _dataScreenStateFlow.emit(dataScreenStateFlow)
        }
    }

    init {
        with(sharedPreferencesManager) {
            val initialState = DataScreenState(
                user = user,
                description = threeWordDescription,
                androidRate = androidRate
            )
            updateAndEmit(initialState)
        }
    }

    override fun onImageUriChanged(uri: Uri) {
        with(_dataScreenStateFlow.value) {
            val updatedState = copy(
                user = user.copy(picUri = uri.toString())
            )
            updateAndEmit(updatedState)
        }
    }

    override fun onFirstNameChanged(firstName: String) {
        with(_dataScreenStateFlow.value) {
            val updatedState = copy(
                user = user.copy(firstName = firstName)
            )
            updateAndEmit(updatedState)
        }
    }

    override fun onLastNameChanged(lastName: String) {
        with(_dataScreenStateFlow.value) {
            val updatedState = copy(
                user = user.copy(lastName = lastName)
            )
            updateAndEmit(updatedState)
        }
    }

    override fun onRateChange(value: Float) {
        with(_dataScreenStateFlow.value) {
            val updatedState = copy(androidRate = value)
            updateAndEmit(updatedState)
        }
    }


    override fun onDescriptionChanged(description: List<String>) {
        with(_dataScreenStateFlow.value) {
            val updatedState = copy(description = description)
            updateAndEmit(updatedState)
        }
    }

    override fun clearData() {
        with(sharedPreferencesManager) {
            clearSharedPreferences()
            val updatedState = _dataScreenStateFlow.value.copy(
                user = user,
                description = threeWordDescription,
                androidRate = androidRate
            )
            updateAndEmit(updatedState)
        }
    }

    override fun onSaveData() {
        super.onSaveData()
        val currentData = _dataScreenStateFlow.value
        sharedPreferencesManager.apply {
            user = currentData.user
            threeWordDescription = currentData.description
            androidRate = currentData.androidRate
        }
        updateAndEmit(currentData)
    }

    private fun calculateCanSave(state: DataScreenState) = state.isDataValid()
            && state.hasDataBeenChanged(
        u = sharedPreferencesManager.user,
        desc = sharedPreferencesManager.threeWordDescription,
        rate = sharedPreferencesManager.androidRate
    )

    private fun updateAndEmit(state: DataScreenState) {
        updateDataScreenState(
            state.copy(
                canSave = calculateCanSave(state),
                canClear = state.hasDataChangedFromDefault()
            )
        )
    }

}