package com.droidconlisbon.sp2ds.ui.composables.home


import com.droidconlisbon.sp2ds.proto.ChatMessage

data class HomeScreenDataState(
    var hasBeenOnboarded: Boolean? = null,
    val chatMessages: MutableList<ChatMessage> = mutableListOf(),
    val timeOutTimestamp: String = "",
    val avatarUri: String = "",
    val errorMessage: String = "",
    val hasPopulatedData: Boolean = true,
    val isLoading: Boolean = false
)
