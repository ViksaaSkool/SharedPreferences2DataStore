package com.droidconlisbon.sp2ds

import android.app.Application
import com.droidconlisbon.sp2ds.coroutine.CoroutineProvider
import com.droidconlisbon.sp2ds.storage.datastore.Sp2DsMigrator
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class Sp2DsApplication : Application() {

    @Inject
    lateinit var sp2DsMigrator: Sp2DsMigrator

    @Inject
    lateinit var coroutineProvider: CoroutineProvider

    private val scope: CoroutineScope
        get() = coroutineProvider.createCoroutineScope()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        migrateProto()
    }

    private fun migrateProto() = scope.launch(Dispatchers.IO) {
        sp2DsMigrator.migrateToProtoStore()
    }
}