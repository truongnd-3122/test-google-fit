package com.example.myapplication.di

import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.repository.MainRepository
import com.example.myapplication.data.repository.MovieRepository
import com.example.myapplication.data.repository.SignInRepository
import com.example.myapplication.data.repository.SignUpRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.sgaas.data.repository.impl.MainRepositoryImpl
import jp.co.sgaas.data.repository.impl.MovieRepositoryImpl
import jp.co.sgaas.data.repository.impl.SignInRepositoryImpl
import jp.co.sgaas.data.repository.impl.SignUpRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideUserRepository(apiService: ApiService): MovieRepository = MovieRepositoryImpl(apiService)


    @Singleton
    @Provides
    fun provideSignUpRepository(apiService: ApiService): SignUpRepository = SignUpRepositoryImpl(apiService)

    @Singleton
    @Provides
    fun provideSignInRepository(apiService: ApiService): SignInRepository = SignInRepositoryImpl(apiService)

    @Singleton
    @Provides
    fun provideSignOutRepository(apiService: ApiService): MainRepository = MainRepositoryImpl(apiService)

}
