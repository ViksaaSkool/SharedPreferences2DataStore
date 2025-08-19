package com.droidconlisbon.sp2ds.ui.composables.home


import com.droidconlisbon.sp2ds.model.ChatMessage

data class HomeScreenDataState(
    val isInitialized: Boolean = false,
    val hasBeenOnboarded: Boolean? = false,
    val chatMessages: List<ChatMessage> = emptyList(),
    val timeOutTimestamp: String = "",
    val avatarUri: String = "",
    val errorMessage: String = "",
    val hasPopulatedData: Boolean = true,
    val isLoading: Boolean = false
)
