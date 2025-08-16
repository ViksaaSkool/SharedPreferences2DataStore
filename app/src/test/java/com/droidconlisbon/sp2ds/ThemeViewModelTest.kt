package com.droidconlisbon.sp2ds

import app.cash.turbine.test
import com.droidconlisbon.sp2ds.storage.sharedpreferences.Sp2DsSharedPreferencesManager
import com.droidconlisbon.sp2ds.ui.composables.data.ThemeViewModel
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ThemeViewModelTest {

    @MockK
    private lateinit var mockSharedPreferences: Sp2DsSharedPreferencesManager

    private lateinit var viewModel: ThemeViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        with( mockSharedPreferences){
            every { isDarkTheme } returns false
            every { isDarkTheme = any() } just Runs
        }
        viewModel = ThemeViewModel(mockSharedPreferences)
    }

    @Test
    fun `initial themeState matches SharedPreferences value`() = runBlocking {
        viewModel.isDarkThemeStateFlow.test {
            val initial = awaitItem()
            assertEquals(false, initial) // initial value matches mocked getter
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onThemeChanged updates StateFlow and SharedPreferences`() = runBlocking {
        val newTheme = true
        viewModel.run {
            onThemeChanged(newTheme)
            isDarkThemeStateFlow.test {
                val current = awaitItem()
                assertEquals(newTheme, current)
                cancelAndIgnoreRemainingEvents()
            }
        }
        verify { mockSharedPreferences.isDarkTheme = newTheme }
    }
}