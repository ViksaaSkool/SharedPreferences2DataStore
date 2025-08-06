package com.droidconlisbon.sp2ds.network

import com.droidconlisbon.sp2ds.coroutine.CoroutineProvider
import com.droidconlisbon.sp2ds.network.data.ApiConstants.ROLE
import com.droidconlisbon.sp2ds.network.data.ChatRequest
import com.droidconlisbon.sp2ds.network.data.ChatResponse
import com.droidconlisbon.sp2ds.network.data.Message
import com.droidconlisbon.sp2ds.network.data.ResultWrapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatServiceImpl @Inject constructor(
    val openRouterApi: OpenRouterApi,
    val coroutineProvider: CoroutineProvider
) : ChatService {

    private val ioContext = coroutineProvider.ioContext()

    override fun chat(query: String): Flow<ResultWrapper<ChatResponse>> =
        NetworkHelper.emitFlowResult(ioContext) {
            openRouterApi.chatCompletion(
                request = ChatRequest(
                    messages = listOf(
                        Message(
                            role = ROLE,
                            content = query
                        )
                    )
                )
            )
        }

}
