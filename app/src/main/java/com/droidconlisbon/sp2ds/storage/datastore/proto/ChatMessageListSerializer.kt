package com.droidconlisbon.sp2ds.storage.datastore.proto

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.droidconlisbon.sp2ds.proto.ChatMessage
import com.droidconlisbon.sp2ds.proto.ChatMessageList
import com.droidconlisbon.sp2ds.proto.MessageType
import com.droidconlisbon.sp2ds.util.toProtoStoreName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private class ChatMessagesListSerializer : Serializer<List<ChatMessage>> {
    override val defaultValue: List<ChatMessage>
        get() = emptyList()

    override suspend fun readFrom(input: InputStream): List<ChatMessage> {
        try {
            val chatMessagesList = ChatMessageList.parseFrom(input)
            return chatMessagesList.messagesList
        } catch (exception: Exception) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: List<ChatMessage>,
        output: OutputStream
    ) {
        val chatMessagesList = ChatMessageList.newBuilder()
            .addAllMessages(t)
            .build()
        chatMessagesList.writeTo(output)
    }
}

class ChatMessagesListDataStorePropertyFlow(
    private val context: Context,
    dataStoreName: String,
    private val coroutineScope: CoroutineScope
) : ReadWriteProperty<Any, Flow<List<ChatMessage>?>> {

    private val Context.chatMessagesListDataStore: DataStore<List<ChatMessage>> by dataStore(
        fileName = dataStoreName.toProtoStoreName(),
        serializer = ChatMessagesListSerializer(),
    )

    private val protoStore: DataStore<List<ChatMessage>>
        get() = context.chatMessagesListDataStore

    override fun getValue(thisRef: Any, property: KProperty<*>): Flow<List<ChatMessage>> =
        protoStore.data

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Flow<List<ChatMessage>?>) {
        coroutineScope.launch(Dispatchers.IO) {
            val messages = value.firstOrNull() ?: emptyList()
            protoStore.updateData {
                messages
            }
        }
    }
}


data class ChatMessageData(val message: String, val messageType: MessageDataType)

enum class MessageDataType {
    ANSWER,
    QUESTION
}


fun ChatMessageData.toProto(): ChatMessage {
    return ChatMessage.newBuilder()
        .setMessage(message)
        .setMessageType(
            when (messageType) {
                MessageDataType.ANSWER -> MessageType.ANSWER
                MessageDataType.QUESTION -> MessageType.QUESTION
            }
        )
        .build()
}

fun List<ChatMessageData>.toProtoList(): List<ChatMessage> {
    return this.map { it.toProto() }
}