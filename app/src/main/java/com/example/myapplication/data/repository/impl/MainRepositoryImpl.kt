package jp.co.sgaas.data.repository.impl

import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.api.BaseApiConfig
import com.example.myapplication.data.remote.api.ResultWrapper
import com.example.myapplication.data.repository.MainRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(private val apiService: ApiService): MainRepository, BaseApiConfig() {
    override suspend fun signOut(): Flow<ResultWrapper<Response<Unit>>> {
        return safeApiCallFlow(call = {
            apiService.signOut()
        })
    }
}