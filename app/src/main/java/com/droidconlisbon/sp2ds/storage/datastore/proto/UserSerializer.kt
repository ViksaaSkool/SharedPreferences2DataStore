package com.droidconlisbon.sp2ds.storage.datastore.proto

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.droidconlisbon.sp2ds.proto.User
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

private class UserSerializer : Serializer<User> {
    override val defaultValue: User = User.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): User {
        return try {
            User.parseFrom(input)
        } catch (exception: Exception) {
            throw CorruptionException("Cannot read proto User", exception)
        }
    }

    override suspend fun writeTo(t: User, output: OutputStream) {
        t.writeTo(output)
    }
}


class UserDataStorePropertyFlow(
    val context: Context,
    val dataStoreName: String,
    private val coroutineScope: CoroutineScope
) : ReadWriteProperty<Any, Flow<User?>> {

    private val Context.userDataStore: DataStore<User> by dataStore(
        fileName = dataStoreName.toProtoStoreName(),
        serializer = UserSerializer(),
    )

    private val protoStore: DataStore<User>
        get() = context.userDataStore

    override fun getValue(thisRef: Any, property: KProperty<*>): Flow<User> =
        protoStore.data

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Flow<User?>) {
        coroutineScope.launch(Dispatchers.IO) {
            val user = value.firstOrNull() ?: User.getDefaultInstance()
            protoStore.updateData {
                user
            }
        }
    }

}

fun User.isValid() = firstName.isNotEmpty() && lastName.isNotEmpty() && picUri.isNotEmpty()

data class UserData(
    val firstName: String = "",
    val lastName: String = "",
    val picUri: String = ""
)

// From domain User to ProtoUser
fun UserData.toProto(): User {
    return User.newBuilder()
        .setFirstName(firstName)
        .setLastName(lastName)
        .setPicUri(picUri)
        .build()
}