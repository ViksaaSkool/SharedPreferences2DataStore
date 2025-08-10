package com.droidconlisbon.sp2ds.storage.datastore


import com.droidconlisbon.sp2ds.model.ChatMessage
import com.droidconlisbon.sp2ds.model.User
import kotlinx.coroutines.flow.Flow

interface Sp2DsDataStore {

    var hasProtoBeenMigrated: Boolean
    var isOnboardingShownFlow: Flow<Boolean>
    var userFlow: Flow<User>
    var threeWordDescriptionFlow: Flow<List<String>>
    var isDarkTheme: Boolean
    var questionsLeft: Int
    var timeoutTimestamp: String
    var androidRate: Float
    var chatMessagesFlow: Flow<List<ChatMessage>>
    suspend fun hasStoredValidData(): Boolean
    fun clearData(): Unit
}