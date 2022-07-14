package com.example.myapplication.di

import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.content.res.Resources
import com.example.myapplication.data.local.pref.AppPrefs
import com.example.myapplication.data.local.pref.PrefHelper
import com.example.myapplication.data.repository.FitRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.HistoryClient
import com.google.android.gms.fitness.RecordingClient
import com.google.android.gms.fitness.data.DataType
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jp.co.sgaas.data.repository.impl.FitRepositoryImpl
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {
    @Singleton
    @Provides
    fun provideAppContext(@ApplicationContext context: Context): Context = context

    @Singleton
    @Provides
    fun provideResources(context: Context): Resources = context.resources

    @Singleton
    @Provides
    fun provideAssetManager(context: Context): AssetManager = context.assets

    @Singleton
    @Provides
    fun provideSharedPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    @Singleton
    @Provides
    fun providePrefHelper(
        sharedPreferences: SharedPreferences,
        moshi: Moshi
    ): PrefHelper = AppPrefs(sharedPreferences, moshi)


    @Singleton
    @Provides
    fun provideAppPrefs(
        sharedPreferences: SharedPreferences,
        moshi: Moshi
    ): AppPrefs = AppPrefs(sharedPreferences, moshi)

    @Singleton
    @Provides
    @Named("fitness")
    fun provideFitnessOptions(): GoogleSignInOptionsExtension =
        FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .build()

    @Singleton
    @Provides
    fun provideHistoryClient(
        appContext: Context,
        @Named("fitness") fitness: GoogleSignInOptionsExtension
    ): HistoryClient =
        Fitness.getHistoryClient(
            appContext,
            GoogleSignIn.getAccountForExtension(appContext, fitness)
        )

    @Singleton
    @Provides
    fun provideRecordingClient(
        @ApplicationContext appContext: Context,
        @Named("fitness") fitness: GoogleSignInOptionsExtension
    ): RecordingClient =
        Fitness.getRecordingClient(
            appContext,
            GoogleSignIn.getAccountForExtension(appContext,fitness)
        )

    @Singleton
    @Provides
    fun provideFitRepository(
        historyClient: HistoryClient,
        recordingClient: RecordingClient
    ): FitRepository = FitRepositoryImpl(historyClient, recordingClient)
}