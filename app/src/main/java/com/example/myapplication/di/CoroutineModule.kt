package com.example.myapplication.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class CoroutineModule {

    @Provides
    @Singleton
    @Named("dispatcher_main")
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Singleton
    @Named("dispatcher_io")
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    @Named("dispatcher_unconfined")
    fun provideUnconfinedDispatcher(): CoroutineDispatcher = Dispatchers.Unconfined

    @Provides
    @Singleton
    @Named("dispatcher_main_immediate")
    fun provideMainImmediateDispatcher(): CoroutineDispatcher = Dispatchers.Main.immediate

    @Provides
    @Singleton
    @Named("dispatcher_default")
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
