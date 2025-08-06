package com.droidconlisbon.sp2ds.network.data

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T, var requestedUrl: String = "") : ResultWrapper<T>()
    data class Error<T>(val value: ErrorResponse) : ResultWrapper<T>()
    data class UnknownError<T>(val exception: Exception) : ResultWrapper<T>()
    object Loading : ResultWrapper<Nothing>()
}