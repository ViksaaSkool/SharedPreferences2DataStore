package com.droidconlisbon.sp2ds.network

import com.droidconlisbon.sp2ds.network.data.ApiConstants.CHAT
import com.droidconlisbon.sp2ds.network.data.ChatResponse
import com.droidconlisbon.sp2ds.network.data.ChatRequest
import com.droidconlisbon.sp2ds.network.data.ResultWrapper
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenRouterApi {
    @POST(CHAT)
    suspend fun chatCompletion(
        @Body request: ChatRequest,
    ): Response<ChatResponse>
}

interface ChatService {
    fun chat(query: String): Flow<ResultWrapper<ChatResponse>>
}


