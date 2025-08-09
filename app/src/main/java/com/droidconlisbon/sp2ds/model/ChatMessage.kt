package com.droidconlisbon.sp2ds.model


data class ChatMessage(val message: String, val messageType: MessageType)

enum class MessageType {
    ANSWER,
    QUESTION
}

fun MessageType.isAnswer() = this == MessageType.ANSWER


