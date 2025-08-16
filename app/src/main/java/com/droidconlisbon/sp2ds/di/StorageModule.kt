package com.droidconlisbon.sp2ds.di


import com.droidconlisbon.sp2ds.storage.datastore.Sp2DsDataStore
import com.droidconlisbon.sp2ds.storage.datastore.Sp2DsDataStoreManager
import com.droidconlisbon.sp2ds.storage.datastore.Sp2DsMigrator
import com.droidconlisbon.sp2ds.storage.datastore.Sp2DsMigratorManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class Sp2DsDataStoreModule {

    @Binds
    @Singleton
    abstract fun bindSp2DsDataStore(
        impl: Sp2DsDataStoreManager
    ): Sp2DsDataStore

    @Binds
    @Singleton
    abstract fun bindSp2DsMigrator(
        impl: Sp2DsMigratorManager
    ): Sp2DsMigrator
}