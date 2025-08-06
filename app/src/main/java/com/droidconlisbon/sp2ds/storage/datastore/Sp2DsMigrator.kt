package com.droidconlisbon.sp2ds.storage.datastore

interface Sp2DsMigrator {
    suspend fun migrateToProtoStore()
}