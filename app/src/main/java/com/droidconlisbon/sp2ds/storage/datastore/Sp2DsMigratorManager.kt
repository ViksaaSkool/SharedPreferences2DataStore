package com.droidconlisbon.sp2ds.storage.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.droidconlisbon.sp2ds.storage.Constants.CHAT_MESSAGES_LIST_KEY
import com.droidconlisbon.sp2ds.storage.Constants.THREE_WORDS_DESCRIPTION_KEY
import com.droidconlisbon.sp2ds.storage.Constants.USER_KEY
import com.droidconlisbon.sp2ds.storage.datastore.proto.ChatMessageData
import com.droidconlisbon.sp2ds.storage.datastore.proto.UserData
import com.droidconlisbon.sp2ds.storage.datastore.proto.toProto
import com.droidconlisbon.sp2ds.storage.datastore.proto.toProtoList
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class Sp2DsMigratorManager @Inject constructor(
    @ApplicationContext val context: Context,
    private val sp2DsDataStore: Sp2DsDataStore,
    private val dataStore: DataStore<Preferences>,
    moshi: Moshi
) : Sp2DsMigrator {

    private val userAdapter = moshi.adapter(UserData::class.java)
    private val chatMessagesAdapter = moshi.adapter<List<ChatMessageData>>(
        Types.newParameterizedType(
            List::class.java,
            ChatMessageData::class.java
        )
    )
    private val listOfStringsAdapter = moshi.adapter<List<String>>(
        Types.newParameterizedType(
            List::class.java,
            String::class.java
        )
    )

    override suspend fun migrateToProtoStore() {
        if (sp2DsDataStore.hasProtoBeenMigrated) {
            return
        }
        coroutineScope {
            val jobs = listOf(
                async {
                    migrateProperty(USER_KEY) { jsonValue ->
                        val userValue = if (jsonValue.isNotEmpty()) {
                            userAdapter.fromJson(jsonValue) ?: UserData()
                        } else {
                            UserData()
                        }
                        sp2DsDataStore.userFlow = flowOf(userValue.toProto())
                    }
                },
                async {
                    migrateProperty(CHAT_MESSAGES_LIST_KEY) { jsonValue ->
                        val messages = if (jsonValue.isNotEmpty()) {
                            chatMessagesAdapter.fromJson(jsonValue) ?: emptyList()
                        } else {
                            emptyList()
                        }
                        sp2DsDataStore.chatMessagesFlow = flowOf(messages.toProtoList())
                    }
                },
                async {
                    migrateProperty(THREE_WORDS_DESCRIPTION_KEY) { jsonValue ->
                        val list = if (jsonValue.isNotEmpty()) {
                            listOfStringsAdapter.fromJson(jsonValue) ?: emptyList()
                        } else {
                            emptyList()
                        }
                        sp2DsDataStore.threeWordDescriptionFlow = flowOf(list)
                    }
                }
            )
            jobs.awaitAll()
        }

    }

    private suspend fun migrateProperty(key: String, migrate: (jsonValue: String) -> Unit) {
        val jsonValue = dataStore.readAndDelete(key, "")
        migrate(jsonValue)
    }

}