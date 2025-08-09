package com.droidconlisbon.sp2ds.di


import androidx.annotation.Keep
import com.droidconlisbon.sp2ds.coroutine.CoroutineProvider
import com.droidconlisbon.sp2ds.coroutine.CoroutineProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.InternalCoroutinesApi

@Keep
@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @InternalCoroutinesApi
    @Provides
    fun provideCoroutineProvider(): CoroutineProvider = CoroutineProviderImpl()

}