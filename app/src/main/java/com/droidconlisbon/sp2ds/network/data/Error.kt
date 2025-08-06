package com.droidconlisbon.sp2ds.network.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @Json(name = "error")
    val error: ErrorDetail
)

@JsonClass(generateAdapter = true)
data class ErrorDetail(
    @Json(name = "message")
    val message: String,

    @Json(name = "code")
    val code: Int = -1
)