package com.droidconlisbon.sp2ds.model

import com.droidconlisbon.sp2ds.proto.ChatMessageProto
import com.droidconlisbon.sp2ds.proto.MessageTypeProto

data class ChatMessage(val message: String, val messageType: MessageType)

enum class MessageType {
    ANSWER,
    QUESTION
}

fun ChatMessage.toProto(): ChatMessageProto {
    return ChatMessageProto.newBuilder()
        .setMessage(message)
        .setMessageType(
            when (messageType) {
                MessageType.ANSWER -> MessageTypeProto.ANSWER
                MessageType.QUESTION -> MessageTypeProto.QUESTION
            }
        )
        .build()
}


fun List<ChatMessage>.toProtoList(): List<ChatMessageProto> {
    return this.map { it.toProto() }
}

fun ChatMessageProto.toDomain(): ChatMessage {
    return ChatMessage(
        message = message,
        messageType = when (messageType) {
            MessageTypeProto.ANSWER -> MessageType.ANSWER
            MessageTypeProto.QUESTION -> MessageType.QUESTION
            else -> MessageType.ANSWER
        }
    )
}