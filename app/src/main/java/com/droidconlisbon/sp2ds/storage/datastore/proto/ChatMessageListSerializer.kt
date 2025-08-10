package com.droidconlisbon.sp2ds.storage.datastore.proto

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.droidconlisbon.sp2ds.model.ChatMessage
import com.droidconlisbon.sp2ds.model.toDomain
import com.droidconlisbon.sp2ds.model.toProtoList
import com.droidconlisbon.sp2ds.proto.ChatMessageList
import com.droidconlisbon.sp2ds.proto.ChatMessageProto
import com.droidconlisbon.sp2ds.util.toProtoStoreName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private class ChatMessagesListSerializer : Serializer<List<ChatMessageProto>> {
    override val defaultValue: List<ChatMessageProto>
        get() = emptyList()

    override suspend fun readFrom(input: InputStream): List<ChatMessageProto> {
        try {
            val chatMessagesList = ChatMessageList.parseFrom(input)
            return chatMessagesList.messagesList
        } catch (exception: Exception) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: List<ChatMessageProto>,
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

    private val Context.chatMessagesListDataStore: DataStore<List<ChatMessageProto>> by dataStore(
        fileName = dataStoreName.toProtoStoreName(),
        serializer = ChatMessagesListSerializer(),
    )

    private val protoStore: DataStore<List<ChatMessageProto>>
        get() = context.chatMessagesListDataStore

    override fun getValue(thisRef: Any, property: KProperty<*>): Flow<List<ChatMessage>> =
        protoStore.data.map { protoList ->
            protoList.map { proto ->
                proto.toDomain()
            }
        }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Flow<List<ChatMessage>?>) {
        coroutineScope.launch(Dispatchers.IO) {
            val messages = value.firstOrNull() ?: emptyList()
            protoStore.updateData {
                messages.toProtoList()
            }
        }
    }
}