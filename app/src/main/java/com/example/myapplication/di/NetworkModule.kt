package com.example.myapplication.di

import android.content.Context
import android.content.res.AssetManager
import com.example.myapplication.BuildConfig
import com.example.myapplication.data.local.pref.AppPrefs
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.MockInterceptor
import com.example.myapplication.data.remote.api.HeaderInterceptor
import com.example.myapplication.enableLogging
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpCache(context: Context): Cache =
        Cache(context.cacheDir, (10 * 1024 * 1024).toLong())

    @Singleton
    @Provides
    @Named("logging")
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (enableLogging()) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }

//    @Singleton
//    @Provides
//    @Named("header")
//    fun provideHeaderInterceptor(prefHelper: PrefHelper): Interceptor =
//        HeaderInterceptor(prefHelper)


    @Singleton
    @Provides
    @Named("mock")
    fun provideMockInterceptor(assetManager: AssetManager): MockInterceptor =
        MockInterceptor(assetManager)

    @Singleton
    @Provides
    fun provideAppRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .build()


    @Singleton
    @Provides
    fun provideHeaderInterceptor(appPrefs: AppPrefs): Interceptor = HeaderInterceptor(appPrefs)

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)


    @Provides
    fun provideOkHttpClient(
        headerInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .build()
    }



}
