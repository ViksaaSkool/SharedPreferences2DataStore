package com.droidconlisbon.sp2ds

import com.droidconlisbon.sp2ds.network.ChatService
import com.droidconlisbon.sp2ds.storage.sharedpreferences.Sp2DsSharedPreferencesManager
import com.droidconlisbon.sp2ds.ui.composables.data.ThemeViewModel
import com.droidconlisbon.sp2ds.ui.composables.home.HomeViewModel
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @MockK
    private lateinit var mockSharedPreferences: Sp2DsSharedPreferencesManager

    @MockK
    private lateinit var mockChatService: ChatService

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = HomeViewModel(
            chatService = mockChatService,
            sharedPreferencesManager = mockSharedPreferences
        )
    }


}