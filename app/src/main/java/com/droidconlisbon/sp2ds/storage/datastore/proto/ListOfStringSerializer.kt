package com.droidconlisbon.sp2ds.storage.datastore.proto

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.droidconlisbon.sp2ds.proto.ListOfString
import com.droidconlisbon.sp2ds.util.toProtoStoreName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import kotlin.String
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private class StringListSerializer : Serializer<List<String>> {
    override val defaultValue: List<String>
        get() = emptyList()

    override suspend fun readFrom(input: InputStream): List<String> {
        try {
            val longList = ListOfString.parseFrom(input)
            return longList.itemList
        } catch (exception: Exception) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: List<String>, output: OutputStream) {
        val longList = ListOfString.newBuilder()
            .addAllItem(t)
            .build()
        longList.writeTo(output)
    }
}


class StringListDataStorePropertyFlow(
    val context: Context,
    dataStoreName: String,
    private val coroutineScope: CoroutineScope
) : ReadWriteProperty<Any, Flow<List<String>?>> {

    private val Context.stringListDataStore: DataStore<List<String>> by dataStore(
        fileName = dataStoreName.toProtoStoreName(),
        serializer = StringListSerializer(),
    )

    private val protoStore: DataStore<List<String>>
        get() = context.stringListDataStore

    override fun getValue(thisRef: Any, property: KProperty<*>): Flow<List<String>> =
        protoStore.data

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Flow<List<String>?>) {
        coroutineScope.launch (Dispatchers.IO) {
            val list = value.firstOrNull() ?: emptyList()
            protoStore.updateData { list }
        }
    }

}