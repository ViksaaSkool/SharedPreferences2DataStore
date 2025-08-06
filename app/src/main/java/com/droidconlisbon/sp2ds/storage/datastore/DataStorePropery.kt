package com.droidconlisbon.sp2ds.storage.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class DataStoreProperty<T>(
    private val coroutineScope: CoroutineScope,
    private val dataStore: DataStore<Preferences>,
    private val key: Preferences.Key<T>,
    private val default: T
) : ReadWriteProperty<Any, T?> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T = runBlocking {
        dataStore.data
            .map { preferences ->
                preferences[key] ?: default
            }.first()
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        coroutineScope.launch {
            dataStore.edit { preferences ->
                value?.let {
                    preferences[key] = it
                }
            }
        }
    }

    val exists: Boolean
        get() {
            var result = false
            runBlocking {
                result = dataStore.data.map { preferences -> preferences[key] }.first() != null
            }
            return result
        }
}

class DataStorePropertyFlow<T>(
    private val coroutineScope: CoroutineScope,
    private val dataStore: DataStore<Preferences>,
    private val key: Preferences.Key<T>,
    private val default: T
) : ReadWriteProperty<Any, Flow<T?>> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Flow<T> =
        dataStore.data
            .map { preferences ->
                preferences[key] ?: default
            }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Flow<T?>) {
        coroutineScope.launch {
            dataStore.edit { preferences ->
                value.first()?.let {
                    preferences[key] = it
                }
            }

        }
    }
}

class SimplifiedDataStoreProperty<T>(
    private val clazz: Class<T>,
    private val coroutineScope: CoroutineScope,
    private val dataStore: DataStore<Preferences>,
    private val keyString: String,
    private val default: T
) : ReadWriteProperty<Any, T?> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T = runBlocking {
        dataStore.data.map { preferences ->
            Timber.d(
                "getValue() | migrateToProtoStore()  keyString = $keyString, default = $default," +
                        "value = ${preferences[clazz.toPreferenceKey(keyString)]}"
            )
            preferences[clazz.toPreferenceKey(keyString)] ?: default
        }.first()
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        coroutineScope.launch {
            dataStore.edit { preferences ->
                value?.let {
                    preferences[clazz.toPreferenceKey(keyString)] = it
                }
            }
        }
    }
}


class SimplifiedPropertyFlow<T>(
    private val clazz: Class<T>,
    private val coroutineScope: CoroutineScope,
    private val dataStore: DataStore<Preferences>,
    private val keyString: String,
    private val default: T
) : ReadWriteProperty<Any, Flow<T?>> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Flow<T> =
        dataStore.data
            .map { preferences ->
                preferences[clazz.toPreferenceKey(keyString)] ?: default
            }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Flow<T?>) {
        coroutineScope.launch {
            dataStore.edit { preferences ->
                value.first()?.let {
                    preferences[clazz.toPreferenceKey(keyString)] = it
                }
            }

        }
    }
}


