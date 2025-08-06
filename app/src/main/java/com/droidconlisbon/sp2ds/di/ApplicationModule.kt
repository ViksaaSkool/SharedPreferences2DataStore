package com.droidconlisbon.sp2ds.di

import android.content.Context
import androidx.annotation.Keep
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.droidconlisbon.sp2ds.coroutine.CoroutineProvider
import com.droidconlisbon.sp2ds.coroutine.CoroutineProviderImpl
import com.droidconlisbon.sp2ds.storage.Constants.DATASTORE_NAME
import com.droidconlisbon.sp2ds.storage.Constants.SHARED_PREFERENCES_NAME
import com.droidconlisbon.sp2ds.storage.datastore.Sp2DsDataStore
import com.droidconlisbon.sp2ds.storage.datastore.Sp2DsDataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Singleton

@Keep
@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @InternalCoroutinesApi
    @Provides
    fun provideCoroutineProvider(): CoroutineProvider = CoroutineProviderImpl()

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext appContext: Context,
        coroutineProvider: CoroutineProvider
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { emptyPreferences() }
        ),
        migrations = listOf(
            SharedPreferencesMigration(
                context = appContext,
                sharedPreferencesName = SHARED_PREFERENCES_NAME
            )
        ),
        scope = coroutineProvider.createCoroutineScope(),
        produceFile = { appContext.preferencesDataStoreFile(DATASTORE_NAME) }
    )

}