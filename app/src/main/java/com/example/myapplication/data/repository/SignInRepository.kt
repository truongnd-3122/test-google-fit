package com.example.myapplication.data.repository

import com.example.myapplication.data.model.UserInfoSignIn
import com.example.myapplication.data.remote.api.ResultWrapper
import com.example.myapplication.data.remote.response.Response
import com.example.myapplication.data.remote.response.DataResponse
import kotlinx.coroutines.flow.Flow

interface SignInRepository {
    suspend fun signIn(userInfoSignIn: UserInfoSignIn): Flow<ResultWrapper<Response<DataResponse>>>
}