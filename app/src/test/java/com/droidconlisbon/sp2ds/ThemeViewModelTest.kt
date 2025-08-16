package com.droidconlisbon.sp2ds

import app.cash.turbine.test
import com.droidconlisbon.sp2ds.storage.datastore.Sp2DsDataStore
import com.droidconlisbon.sp2ds.ui.composables.data.ThemeViewModel
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class ThemeViewModelTest {

    @MockK
    private lateinit var mockSp2DsStore: Sp2DsDataStore

    private lateinit var viewModel: ThemeViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        with(mockSp2DsStore) {
            every { isDarkTheme } returns false
            every { isDarkTheme = any() } just Runs
        }
        viewModel = ThemeViewModel(mockSp2DsStore)
    }

    @Test
    fun `initial themeState matches DataStore value`() = runTest {
        viewModel.isDarkThemeStateFlow.test {
            val initial = awaitItem()
            assertEquals(false, initial)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onThemeChanged updates StateFlow and DataStore`() = runTest {
        val newTheme = true

        viewModel.run {
            onThemeChanged(newTheme)
            isDarkThemeStateFlow.test {
                val current = awaitItem()
                assertEquals(newTheme, current)
                cancelAndIgnoreRemainingEvents()
            }
        }

        verify { mockSp2DsStore.isDarkTheme = newTheme }
    }

}