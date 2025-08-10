package com.droidconlisbon.sp2ds.storage.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.droidconlisbon.sp2ds.coroutine.CoroutineProvider
import com.droidconlisbon.sp2ds.model.ChatMessage
import com.droidconlisbon.sp2ds.model.User
import com.droidconlisbon.sp2ds.storage.Constants.CHAT_MESSAGES_LIST_KEY
import com.droidconlisbon.sp2ds.storage.Constants.DEFAULT_EXPERIENCE_LEVEL_VALUE
import com.droidconlisbon.sp2ds.storage.Constants.DEFAULT_QUESTIONS_TO_ASK_VALUE
import com.droidconlisbon.sp2ds.storage.Constants.EXPERIENCE_LEVEL_KEY
import com.droidconlisbon.sp2ds.storage.Constants.HAS_BEEN_MIGRATED_TO_PROTO_KEY
import com.droidconlisbon.sp2ds.storage.Constants.IS_DARK_THEME_KEY
import com.droidconlisbon.sp2ds.storage.Constants.ONBOARDING_SHOWN_KEY
import com.droidconlisbon.sp2ds.storage.Constants.QUESTIONS_TO_ASK_KEY
import com.droidconlisbon.sp2ds.storage.Constants.THREE_WORDS_DESCRIPTION_KEY
import com.droidconlisbon.sp2ds.storage.Constants.TIMEOUT_TIMESTAMP_KEY
import com.droidconlisbon.sp2ds.storage.Constants.USER_KEY
import com.droidconlisbon.sp2ds.storage.datastore.proto.ChatMessagesListDataStorePropertyFlow
import com.droidconlisbon.sp2ds.storage.datastore.proto.StringListDataStorePropertyFlow
import com.droidconlisbon.sp2ds.storage.datastore.proto.UserDataStorePropertyFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

class Sp2DsDataStoreManager @Inject constructor(
    @ApplicationContext val context: Context,
    coroutineProvider: CoroutineProvider,
    private val dataStore: DataStore<Preferences>
) : Sp2DsDataStore {

    val coroutineScope = coroutineProvider.createCoroutineScope()

    override var hasProtoBeenMigrated: Boolean by simplifiedDataStoreProperty(
        coroutineScope = coroutineScope,
        dataStore = dataStore,
        key = HAS_BEEN_MIGRATED_TO_PROTO_KEY,
        default = false
    )
    override var isOnboardingShownFlow: Flow<Boolean> by simplifiedDataStorePropertyFlow(
        coroutineScope = coroutineScope,
        dataStore = dataStore,
        key = ONBOARDING_SHOWN_KEY,
        default = false
    )
    override var userFlow: Flow<User> by UserDataStorePropertyFlow(
        context = context,
        dataStoreName = USER_KEY,
        coroutineScope = coroutineScope
    )
    override var threeWordDescriptionFlow: Flow<List<String>> by StringListDataStorePropertyFlow(
        context = context,
        dataStoreName = THREE_WORDS_DESCRIPTION_KEY,
        coroutineScope = coroutineScope
    )
    override var isDarkTheme: Boolean by simplifiedDataStoreProperty(
        coroutineScope = coroutineScope,
        dataStore = dataStore,
        key = IS_DARK_THEME_KEY,
        default = true
    )
    override var questionsLeft: Int by simplifiedDataStoreProperty(
        coroutineScope = coroutineScope,
        dataStore = dataStore,
        key = QUESTIONS_TO_ASK_KEY,
        default = DEFAULT_QUESTIONS_TO_ASK_VALUE
    )
    override var timeoutTimestamp: String by simplifiedDataStoreProperty(
        coroutineScope = coroutineScope,
        dataStore = dataStore,
        key = TIMEOUT_TIMESTAMP_KEY,
        default = ""
    )
    override var androidRate: Float by simplifiedDataStoreProperty(
        coroutineScope = coroutineScope,
        dataStore = dataStore,
        key = EXPERIENCE_LEVEL_KEY,
        default = DEFAULT_EXPERIENCE_LEVEL_VALUE
    )

    override var chatMessagesFlow: Flow<List<ChatMessage>> by ChatMessagesListDataStorePropertyFlow(
        context = context,
        dataStoreName = CHAT_MESSAGES_LIST_KEY,
        coroutineScope = coroutineScope
    )

    override suspend fun hasStoredValidData(): Boolean {
        return userFlow.first().isValid() && threeWordDescriptionFlow.first().isNotEmpty()
    }


    override fun clearData() {
        coroutineScope.launch {
            dataStore.edit { preferences ->
                preferences.clear()
            }
            clearProto()
        }
    }

    private fun clearProto() {
        userFlow = flowOf(User())
        threeWordDescriptionFlow = flowOf(emptyList())
        chatMessagesFlow = flowOf(emptyList())
    }
}