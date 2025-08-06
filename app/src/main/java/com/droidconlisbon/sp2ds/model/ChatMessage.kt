package com.droidconlisbon.sp2ds.model

import com.droidconlisbon.sp2ds.proto.ChatMessage as ProtoChatMessage
import com.droidconlisbon.sp2ds.proto.MessageType as ProtoMessageType

data class ChatMessage(val message: String, val messageType: MessageType)

enum class MessageType {
    ANSWER,
    QUESTION
}

fun MessageType.isAnswer() = this == MessageType.ANSWER


fun ChatMessage.toProto(): ProtoChatMessage {
    return ProtoChatMessage.newBuilder()
        .setMessage(message)
        .setMessageType(
            when (messageType) {
                MessageType.ANSWER -> ProtoMessageType.ANSWER
                MessageType.QUESTION -> ProtoMessageType.QUESTION
            }
        )
        .build()
}


fun List<ChatMessage>.toProtoList(): List<ProtoChatMessage> {
    return this.map { it.toProto() }
}

fun ProtoChatMessage.toDomain(): ChatMessage {
    return ChatMessage(
        message = message,
        messageType = when (messageType) {
            ProtoMessageType.ANSWER -> MessageType.ANSWER
            ProtoMessageType.QUESTION -> MessageType.QUESTION
            ProtoMessageType.MESSAGE_TYPE_UNSPECIFIED,
            ProtoMessageType.UNRECOGNIZED -> MessageType.ANSWER // pick a default or throw error
        }
    )
}

fun List<ProtoChatMessage>.toDomainList(): List<ChatMessage> {
    return this.map { it.toDomain() }
}