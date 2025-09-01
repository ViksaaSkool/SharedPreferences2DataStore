package com.droidconlisbon.sp2ds.storage.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.File


@Suppress("UNCHECKED_CAST")
fun <T> Class<T>.toPreferenceKey(key: String): Preferences.Key<T> {
    return when (this) {
        Boolean::class.java, java.lang.Boolean.TYPE -> booleanPreferencesKey(key) as Preferences.Key<T>
        Int::class.java, Integer.TYPE -> intPreferencesKey(key) as Preferences.Key<T>
        Long::class.java, java.lang.Long.TYPE -> longPreferencesKey(key) as Preferences.Key<T>
        String::class.java -> stringPreferencesKey(key) as Preferences.Key<T>
        Float::class.java, java.lang.Float.TYPE -> floatPreferencesKey(key) as Preferences.Key<T>
        Set::class.java, MutableSet::class.java -> stringSetPreferencesKey(key) as Preferences.Key<T>
        else -> throw IllegalArgumentException("Unsupported type: ${this.simpleName}")
    }
}

fun Any.toPreferencesKey(keyName: String): Preferences.Key<*>? = when (this) {
    is String -> stringPreferencesKey(keyName)
    is Int -> intPreferencesKey(keyName)
    is Boolean -> booleanPreferencesKey(keyName)
    is Float -> floatPreferencesKey(keyName)
    is Long -> longPreferencesKey(keyName)
    is Set<*> -> if (allStrings(this)) stringSetPreferencesKey(keyName) else null
    else -> null
}

@Suppress("UNCHECKED_CAST")
suspend fun <T : Any> DataStore<Preferences>.readAndDelete(
    keyName: String,
    default: T
): T {
    val prefKey = default.toPreferencesKey(keyName) as Preferences.Key<T>?
        ?: throw IllegalArgumentException("Unsupported type for key: $keyName")

    val value = data.map { prefs -> prefs[prefKey] ?: default }.first()

    edit { prefs ->
        prefs.remove(prefKey)
    }

    return value
}

private fun allStrings(set: Set<*>): Boolean {
    return set.all { it is String }
}


inline fun <reified T> simplifiedDataStoreProperty(
    key: String,
    default: T,
    dataStore: DataStore<Preferences>,
    coroutineScope: CoroutineScope,
): SimplifiedDataStoreProperty<T> {
    return SimplifiedDataStoreProperty(
        clazz = when (T::class) {
            Boolean::class -> Boolean::class.java
            Int::class -> Int::class.java
            Long::class -> Long::class.java
            Float::class -> Float::class.java
            String::class -> String::class.java
            Set::class -> Set::class.java
            else -> throw IllegalArgumentException("Unsupported type")
        } as Class<T>,
        coroutineScope = coroutineScope,
        dataStore = dataStore,
        keyString = key,
        default = default
    )
}


inline fun <reified T> simplifiedDataStorePropertyFlow(
    key: String,
    default: T,
    dataStore: DataStore<Preferences>,
    coroutineScope: CoroutineScope,
): SimplifiedPropertyFlow<T> {
    return SimplifiedPropertyFlow(
        clazz = when (T::class) {
            Boolean::class -> Boolean::class.java
            Int::class -> Int::class.java
            Long::class -> Long::class.java
            String::class -> String::class.java
            Set::class -> Set::class.java
            else -> throw IllegalArgumentException("Unsupported type")
        } as Class<T>,
        coroutineScope = coroutineScope,
        dataStore = dataStore,
        keyString = key,
        default = default
    )
}