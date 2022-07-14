package com.example.myapplication.data.repository

import com.example.myapplication.data.remote.api.ResultWrapper
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface MainRepository {
    suspend fun signOut(): Flow<ResultWrapper<Response<Unit>>>
}