package com.droidconlisbon.sp2ds.storage.sharedpreferences

import android.content.Context
import com.droidconlisbon.sp2ds.model.ChatMessage
import com.droidconlisbon.sp2ds.model.User
import com.droidconlisbon.sp2ds.storage.Constants.SHARED_PREFERENCES_NAME
import com.droidconlisbon.sp2ds.storage.Constants.TIMEOUT_TIMESTAMP_KEY
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit
import com.droidconlisbon.sp2ds.storage.Constants.CHAT_MESSAGES_LIST_KEY
import com.droidconlisbon.sp2ds.storage.Constants.DEFAULT_EXPERIENCE_LEVEL_VALUE
import com.droidconlisbon.sp2ds.storage.Constants.DEFAULT_QUESTIONS_TO_ASK_VALUE
import com.droidconlisbon.sp2ds.storage.Constants.EXPERIENCE_LEVEL_KEY
import com.droidconlisbon.sp2ds.storage.Constants.IS_DARK_THEME_KEY
import com.droidconlisbon.sp2ds.storage.Constants.ONBOARDING_SHOWN_KEY
import com.droidconlisbon.sp2ds.storage.Constants.QUESTIONS_TO_ASK_KEY
import com.droidconlisbon.sp2ds.storage.Constants.THREE_WORDS_DESCRIPTION_KEY
import com.droidconlisbon.sp2ds.storage.Constants.USER_KEY


@Singleton
class Sp2DsSharedPreferencesManager @Inject constructor(
    @ApplicationContext context: Context,
    private val moshi: Moshi
) {

    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val userAdapter = moshi.adapter(User::class.java)
    private val chatMessagesAdapter = moshi.adapter<List<ChatMessage>>(
        Types.newParameterizedType(
            List::class.java,
            ChatMessage::class.java
        )
    )
    var hasBeenOnboarded: Boolean
        get() = sharedPreferences.getBoolean(ONBOARDING_SHOWN_KEY, false)
        set(value) {
            sharedPreferences.edit { putBoolean(ONBOARDING_SHOWN_KEY, value) }
        }
    var user: User
        get() {
            val json = sharedPreferences.getString(USER_KEY, null) ?: return User()
            return userAdapter.fromJson(json) ?: User()
        }
        set(value) {
            val json = userAdapter.toJson(value)
            sharedPreferences.edit { putString(USER_KEY, json) }
        }
    var threeWordDescription: List<String>
        get() {
            val json =
                sharedPreferences.getString(THREE_WORDS_DESCRIPTION_KEY, null) ?: return emptyList()
            val type = Types.newParameterizedType(List::class.java, String::class.java)
            return moshi.adapter<List<String>>(type).fromJson(json) ?: emptyList()
        }
        set(value) {
            val type = Types.newParameterizedType(List::class.java, String::class.java)
            val json = moshi.adapter<List<String>>(type).toJson(value)
            sharedPreferences.edit { putString(THREE_WORDS_DESCRIPTION_KEY, json) }
        }
    var isDarkTheme: Boolean
        get() = sharedPreferences.getBoolean(IS_DARK_THEME_KEY, true)
        set(value) {
            sharedPreferences.edit { putBoolean(IS_DARK_THEME_KEY, value).apply() }
        }
    var questionsLeft: Int
        get() = sharedPreferences.getInt(QUESTIONS_TO_ASK_KEY, DEFAULT_QUESTIONS_TO_ASK_VALUE)
        set(value) {
            sharedPreferences.edit { putInt(QUESTIONS_TO_ASK_KEY, value) }
        }
    var timeoutTimestamp: String
        get() = sharedPreferences.getString(TIMEOUT_TIMESTAMP_KEY, "") ?: ""
        set(value) {
            sharedPreferences.edit { putString(TIMEOUT_TIMESTAMP_KEY, value) }
        }
    var androidRate: Float
        get() = sharedPreferences.getFloat(EXPERIENCE_LEVEL_KEY, DEFAULT_EXPERIENCE_LEVEL_VALUE)
        set(value) {
            sharedPreferences.edit { putFloat(EXPERIENCE_LEVEL_KEY, value) }
        }
    var chatMessages: List<ChatMessage>
        get() {
            val json =
                sharedPreferences.getString(CHAT_MESSAGES_LIST_KEY, null) ?: return emptyList()
            return chatMessagesAdapter.fromJson(json) ?: emptyList()
        }
        set(value) {
            val json = chatMessagesAdapter.toJson(value)
            sharedPreferences.edit { putString(CHAT_MESSAGES_LIST_KEY, json) }
        }


    fun hasStoredValidData() = user.isValid() && threeWordDescription.isNotEmpty()

    fun clearSharedPreferences() = sharedPreferences.edit { clear() }
}