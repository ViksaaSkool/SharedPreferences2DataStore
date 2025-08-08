package com.droidconlisbon.sp2ds

import com.droidconlisbon.sp2ds.storage.sharedpreferences.Sp2DsSharedPreferencesManager
import com.droidconlisbon.sp2ds.ui.composables.data.ThemeViewModel
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before

@OptIn(ExperimentalCoroutinesApi::class)
class ThemeViewModelTest {

    @MockK
    private lateinit var mockSharedPreferences: Sp2DsSharedPreferencesManager

    private lateinit var viewModel: ThemeViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = ThemeViewModel(mockSharedPreferences)
    }


}