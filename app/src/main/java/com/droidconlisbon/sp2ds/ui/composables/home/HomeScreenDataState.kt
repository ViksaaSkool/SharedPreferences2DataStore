package com.droidconlisbon.sp2ds.ui.composables.home

import com.droidconlisbon.sp2ds.model.ChatMessage

data class HomeScreenDataState(
    var hasBeenOnboarded: Boolean = false,
    val chatMessages: MutableList<ChatMessage> = mutableListOf(),
    val timeOutTimestamp: String = "",
    val avatarUri: String = "",
    val errorMessage: String = "",
    val hasPopulatedData: Boolean = true,
    val isLoading: Boolean = false
)
