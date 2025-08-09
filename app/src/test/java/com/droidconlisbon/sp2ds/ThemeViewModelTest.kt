package com.droidconlisbon.sp2ds

import com.droidconlisbon.sp2ds.storage.datastore.Sp2DsDataStore
import com.droidconlisbon.sp2ds.ui.composables.data.ThemeViewModel
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before

@OptIn(ExperimentalCoroutinesApi::class)
class ThemeViewModelTest {

    @MockK
    private lateinit var mockSp2DsStore: Sp2DsDataStore

    private lateinit var viewModel: ThemeViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = ThemeViewModel(mockSp2DsStore)
    }


}