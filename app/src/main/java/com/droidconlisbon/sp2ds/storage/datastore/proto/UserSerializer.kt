package com.droidconlisbon.sp2ds.storage.datastore.proto

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.droidconlisbon.sp2ds.model.User
import com.droidconlisbon.sp2ds.model.toDomain
import com.droidconlisbon.sp2ds.model.toProto
import com.droidconlisbon.sp2ds.proto.UserProto
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

private class UserSerializer : Serializer<UserProto> {
    override val defaultValue: UserProto = UserProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserProto {
        return try {
            UserProto.parseFrom(input)
        } catch (exception: Exception) {
            throw CorruptionException("Cannot read proto User", exception)
        }
    }

    override suspend fun writeTo(t: UserProto, output: OutputStream) {
        t.writeTo(output)
    }
}


class UserDataStorePropertyFlow(
    val context: Context,
    val dataStoreName: String,
    private val coroutineScope: CoroutineScope
) : ReadWriteProperty<Any, Flow<User?>> {

    private val Context.userDataStore: DataStore<UserProto> by dataStore(
        fileName = dataStoreName.toProtoStoreName(),
        serializer = UserSerializer(),
    )

    private val protoStore: DataStore<UserProto>
        get() = context.userDataStore

    override fun getValue(thisRef: Any, property: KProperty<*>): Flow<User> =
        protoStore.data.map { it.toDomain() }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Flow<User?>) {
        coroutineScope.launch(Dispatchers.IO) {
            val user = value.firstOrNull() ?: User()
            protoStore.updateData {
                user.toProto()
            }
        }
    }
}