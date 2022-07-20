package com.example.myapplication.data.repository

import com.example.myapplication.data.model.DataFromServer
import com.example.myapplication.data.model.DataToServer
import com.example.myapplication.data.remote.api.ResultWrapper
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface MainRepository {
    suspend fun signOut(): Flow<ResultWrapper<Response<Unit>>>

    suspend fun sendDataToServer(dataToServer: DataToServer): Flow<ResultWrapper<com.example.myapplication.data.remote.response.Response<DataFromServer>>>

    fun subscribeDailySteps(listener: OnSuccessListener<DataSet>)
    fun readDataDailySteps(listener: OnSuccessListener<DataSet>)

    fun subscribeDailyCaloriesAndDistance(listener: OnSuccessListener<Any>)
    fun readDailyCaloriesAndDistance(listener: OnSuccessListener<Any>)

    fun subscribeWeightHeightAndHeartRate(listener: OnSuccessListener<Any>)
    fun readWeightHeightAndHeartRate(listener: OnSuccessListener<Any>)

    fun subscribeBloodPressure(listener: OnSuccessListener<Any>)
    fun readBloodPressure(listener: OnSuccessListener<Any>)

    fun subscribeBodyTemperature(listener: OnSuccessListener<Any>)
    fun readBodyTemperature(listener: OnSuccessListener<Any>)
}