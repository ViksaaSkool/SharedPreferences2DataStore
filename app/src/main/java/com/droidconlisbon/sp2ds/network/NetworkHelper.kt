package com.droidconlisbon.sp2ds.network

import com.droidconlisbon.sp2ds.network.data.ErrorDetail
import com.droidconlisbon.sp2ds.network.data.ErrorResponse
import com.droidconlisbon.sp2ds.network.data.ResultWrapper
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

object NetworkHelper {

    /**
     * Executes a suspend API call safely, wrapping the result into a [ResultWrapper].
     *
     * This function performs the API call passed as [apiCall] and handles the response:
     * - If the response is successful and the body is non-null, it returns [ResultWrapper.Success].
     * - If the response is unsuccessful, it parses the error response and returns [ResultWrapper.Error].
     * - If an exception occurs during the call, it returns [ResultWrapper.UnknownError].
     *
     * @param apiCall A suspend lambda that performs the Retrofit API call and returns a [Response] of type [T].
     * @return A [ResultWrapper] encapsulating success with the response body, a parsed error, or an unknown error.
     */
    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): ResultWrapper<T> {
        try {
            val response = apiCall()
            Timber.d("safeApiCall() | isSuccessful =  ${response.isSuccessful}")
            if (response.isSuccessful) {
                val body = response.body()
                val requestUrl = response.raw().request.url.toString()
                body?.let {
                    return ResultWrapper.Success(it, requestUrl)
                }
            }
            return parseErrorResponse(response)

        } catch (e: Exception) {
            Timber.d("safeApiCall() | Exception = ${e.message}")
            return ResultWrapper.UnknownError(e)
        }

    }

    /**
     * Parses the error response body from a Retrofit [Response] and maps it into a [ResultWrapper.Error].
     *
     * This function attempts to deserialize the error body JSON into an [ErrorResponse] object using Moshi.
     * If deserialization fails or the error body is empty, it returns a default [ErrorResponse] with
     * a generic message and the HTTP status code.
     *
     * @param response The Retrofit [Response] containing the error body to parse.
     * @return A [ResultWrapper.Error] wrapping the parsed [ErrorResponse] or a default error response.
     */
    private fun <T> parseErrorResponse(response: Response<*>): ResultWrapper.Error<T> {
        val errorJson = response.errorBody()?.string().orEmpty()
        Timber.e("safeApiCall() | Error body = $errorJson")

        val moshi = Moshi.Builder().build()
        val errorAdapter = moshi.adapter(ErrorResponse::class.java)

        val errorResponse = try {
            errorAdapter.fromJson(errorJson)
        } catch (e: Exception) {
            Timber.e("safeApiCall() | Error parsing error body: ${e.message}")
            null
        }

        return ResultWrapper.Error(
            errorResponse ?: ErrorResponse(
                error = ErrorDetail(
                    message = "Unexpected error",
                    code = response.code()
                )
            )
        )
    }

    /**
     * Emit the Flow result from [safeApiCall] on provided context
     */
    fun <T> emitFlowResult(
        coroutineContext: CoroutineContext,
        apiCall: suspend () -> Response<T>,
    ) = flow {
        emit(safeApiCall { apiCall() })
    }.flowOn(coroutineContext)

}