package com.droidconlisbon.sp2ds


import com.droidconlisbon.sp2ds.network.ChatService
import com.droidconlisbon.sp2ds.storage.datastore.Sp2DsDataStore
import com.droidconlisbon.sp2ds.ui.composables.data.DataViewModel
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before

@OptIn(ExperimentalCoroutinesApi::class)
class DataViewModelTest {

    @MockK
    private lateinit var mockSp2DsStore: Sp2DsDataStore

    @MockK
    private lateinit var mockChatService: ChatService

    private lateinit var viewModel: DataViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }


}