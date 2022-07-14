package jp.co.sgaas.data.repository.impl

import com.example.myapplication.data.model.UserInfoSignUp
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.api.BaseApiConfig
import com.example.myapplication.data.remote.api.ResultWrapper
import com.example.myapplication.data.remote.response.Response
import com.example.myapplication.data.remote.response.DataResponse
import com.example.myapplication.data.repository.SignUpRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class SignUpRepositoryImpl @Inject constructor(private val apiService: ApiService): SignUpRepository, BaseApiConfig() {
    override suspend fun signUp(userInfoSignUp: UserInfoSignUp): Flow<ResultWrapper<Response<DataResponse>>> {
        return safeApiCallFlow(call = {
//            apiService.signUp(userInfoSignUp.email, userInfoSignUp.password, userInfoSignUp.password_confirmation)
            apiService.signUp(userInfoSignUp)
        })
    }


}