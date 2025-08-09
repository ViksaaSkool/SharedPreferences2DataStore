package com.droidconlisbon.sp2ds.ui.composables.data


import androidx.lifecycle.ViewModel
import com.droidconlisbon.sp2ds.storage.datastore.Sp2DsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

abstract class IThemeViewModel : ViewModel() {
    open fun onThemeChanged(isDarkTheme: Boolean) = Unit
    open val isDarkThemeStateFlow =
        MutableStateFlow(false).asStateFlow()
}

@HiltViewModel
class ThemeViewModel @Inject constructor(val sp2DataStore: Sp2DsDataStore) :
    IThemeViewModel() {

    private val _isDarkThemeStateFlow = MutableStateFlow(sp2DataStore.isDarkTheme)
    override val isDarkThemeStateFlow = _isDarkThemeStateFlow.asStateFlow()

    override fun onThemeChanged(isDarkTheme: Boolean) {
        _isDarkThemeStateFlow.value = isDarkTheme
        sp2DataStore.isDarkTheme = isDarkTheme
    }
}