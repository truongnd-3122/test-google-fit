package jp.co.sgaas.data.repository.impl

import com.example.myapplication.data.model.UserInfoSignIn
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.api.BaseApiConfig
import com.example.myapplication.data.remote.api.ResultWrapper
import com.example.myapplication.data.remote.response.Response
import com.example.myapplication.data.remote.response.DataResponse
import com.example.myapplication.data.repository.SignInRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignInRepositoryImpl @Inject constructor(private val apiService: ApiService): SignInRepository, BaseApiConfig() {
    override suspend fun signIn(userInfoSignIn: UserInfoSignIn): Flow<ResultWrapper<Response<DataResponse>>> {
        return safeApiCallFlow(call = {
//            apiService.signIn(userInfoSignIn.email!!, userInfoSignIn.password!!)
            apiService.signIn(userInfoSignIn)
        })
    }
}