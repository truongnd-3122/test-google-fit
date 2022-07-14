package com.example.myapplication.data.repository

import com.example.myapplication.data.model.UserInfoSignUp
import com.example.myapplication.data.remote.api.ResultWrapper
import com.example.myapplication.data.remote.response.Response
import com.example.myapplication.data.remote.response.DataResponse
import kotlinx.coroutines.flow.Flow

interface SignUpRepository {
//    suspend fun signUp(userInfo: UserInfo): Response<SignUpResponse>
    suspend fun signUp(userInfoSignUp: UserInfoSignUp): Flow<ResultWrapper<Response<DataResponse>>>
}