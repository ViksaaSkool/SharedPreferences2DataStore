package com.droidconlisbon.sp2ds.di

import android.content.Context
import com.droidconlisbon.sp2ds.BuildConfig
import com.droidconlisbon.sp2ds.network.NetworkConnectionInterceptor
import com.droidconlisbon.sp2ds.network.OpenRouterApi
import com.droidconlisbon.sp2ds.network.data.ApiConstants.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext appContext: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(NetworkConnectionInterceptor(appContext))
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer ${BuildConfig.OPEN_ROUTER_KEY}")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        moshi: Moshi,
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenRouterApi(retrofit: Retrofit): OpenRouterApi =
        retrofit.create(OpenRouterApi::class.java)
}