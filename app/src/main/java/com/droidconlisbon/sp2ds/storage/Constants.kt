package com.droidconlisbon.sp2ds.storage

object Constants {

    const val DEFAULT_APP_DATE_FORMAT = "dd.MM.yyyy HH:mm:ss"
    const val DEFAULT_TIMEOUT_TIME_IN_MIN = 15
    const val DEFAULT_QUESTIONS_TO_ASK_VALUE = 8
    const val DEFAULT_EXPERIENCE_LEVEL_VALUE = 5.0f
    const val DEFAULT_WRITING_SPEED_VALUE = 30L

    const val SHARED_PREFERENCES_NAME = "Sp2Ds_sharedpreferences"
    const val DATASTORE_NAME = "Sp2Ds_datastore"

    const val HAS_BEEN_MIGRATED_TO_PROTO_KEY = "HAS_BEEN_MIGRATED_TO_PROTO_KEY" //boolean
    const val USER_KEY = "USER_KEY" //object
    const val THREE_WORDS_DESCRIPTION_KEY = "THREE_WORDS_DESCRIPTION_KEY" //object
    const val CHAT_MESSAGES_LIST_KEY = "CHAT_MESSAGES_LIST_KEY" //object
    const val ONBOARDING_SHOWN_KEY = "ONBOARDING_SHOWN_KEY" //boolean
    const val EXPERIENCE_LEVEL_KEY = "EXPERIENCE_LEVEL_KEY" //float
    const val IS_DARK_THEME_KEY = "IS_DARK_THEME_KEY" //boolean
    const val QUESTIONS_TO_ASK_KEY = "QUESTIONS_TO_ASK_KEY" //int
    const val TIMEOUT_TIMESTAMP_KEY = "TIMEOUT_TIMESTAMP_KEY" //string
}