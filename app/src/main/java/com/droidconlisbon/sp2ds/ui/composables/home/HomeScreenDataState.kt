package com.droidconlisbon.sp2ds.ui.composables.home


import android.annotation.SuppressLint
import com.droidconlisbon.sp2ds.model.ChatMessage
import timber.log.Timber

data class HomeScreenDataState(
    var isInitialized: Boolean = false,
    var hasBeenOnboarded: Boolean? = false,
    val chatMessages: MutableList<ChatMessage> = mutableListOf(),
    val timeOutTimestamp: String = "",
    val avatarUri: String = "",
    val errorMessage: String = "",
    val hasPopulatedData: Boolean = true,
    val isLoading: Boolean = false
)

@SuppressLint("BinaryOperationInTimber")
fun HomeScreenDataState.logData(tag : String) {
    Timber.d(
        "HomeScreenDataState() $tag | " +
                "isInitialized = $isInitialized, " +
                "hasBeenOnboarded = $hasBeenOnboarded, " +
                "chatMessages = $chatMessages, " +
                "timeOutTimestamp = $timeOutTimestamp, " +
                "avatarUri = $avatarUri, " +
                "errorMessage = $errorMessage, " +
                "hasPopulatedData = $hasPopulatedData, " +
                "isLoading = $isLoading"
    )
}
