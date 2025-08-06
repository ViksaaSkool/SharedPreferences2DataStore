package com.droidconlisbon.sp2ds.di

import androidx.annotation.Keep
import com.droidconlisbon.sp2ds.network.ChatService
import com.droidconlisbon.sp2ds.network.ChatServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Keep
@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    abstract fun chatService(chatServiceImpl: ChatServiceImpl): ChatService
}