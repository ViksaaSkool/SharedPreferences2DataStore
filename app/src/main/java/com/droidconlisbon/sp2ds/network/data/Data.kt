package com.droidconlisbon.sp2ds.network.data

import com.droidconlisbon.sp2ds.network.data.ApiConstants.MODEL
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ChatRequest(
    @Json(name = "model")
    val model: String = MODEL,
    @Json(name = "messages")
    val messages: List<Message>
)

@JsonClass(generateAdapter = true)
data class Message(
    @Json(name = "role")
    val role: String,
    @Json(name = "content")
    val content: String
)

@JsonClass(generateAdapter = true)
data class ChatResponse(
    @Json(name = "id") val id: String,
    @Json(name = "provider") val provider: String,
    @Json(name = "model") val model: String,
    @Json(name = "object") val type: String,
    @Json(name = "created") val created: Long,
    @Json(name = "choices") val choices: List<ChatChoice>,
    @Json(name = "usage") val usage: TokenUsage
) {
    fun getAnswer() = if (choices.isEmpty()) {
        ""
    } else {
        choices.first().message.content
    }
}

@JsonClass(generateAdapter = true)
data class ChatChoice(
    @Json(name = "logprobs") val logprobs: Any?, // Replace with specific type if needed
    @Json(name = "finish_reason") val finishReason: String?,
    @Json(name = "native_finish_reason") val nativeFinishReason: String?,
    @Json(name = "index") val index: Int,
    @Json(name = "message") val message: ChatMessageResponse
)

@JsonClass(generateAdapter = true)
data class ChatMessageResponse(
    @Json(name = "role") val role: String,
    @Json(name = "content") val content: String,
    @Json(name = "refusal") val refusal: String?,
    @Json(name = "reasoning") val reasoning: String?
)

@JsonClass(generateAdapter = true)
data class TokenUsage(
    @Json(name = "prompt_tokens") val promptTokens: Int,
    @Json(name = "completion_tokens") val completionTokens: Int,
    @Json(name = "total_tokens") val totalTokens: Int,
    @Json(name = "prompt_tokens_details") val promptTokensDetails: Any?, // Replace with specific type if known
    @Json(name = "completion_tokens_details") val completionTokensDetails: Any?
)