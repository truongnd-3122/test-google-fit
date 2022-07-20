package com.example.myapplication.data.repository.impl

import android.util.Log
import com.example.myapplication.data.model.DataFromServer
import com.example.myapplication.data.model.DataToServer
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.api.BaseApiConfig
import com.example.myapplication.data.remote.api.ResultWrapper
import com.example.myapplication.data.repository.MainRepository
import com.google.android.gms.fitness.HistoryClient
import com.google.android.gms.fitness.RecordingClient
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.HealthDataTypes
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.coroutines.NonCancellable.start
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val historyClient: HistoryClient,
    private val recordingClient: RecordingClient
) : MainRepository, BaseApiConfig() {

    private fun start(): Long {
        val cal = Calendar.getInstance()
        cal.time = Date()

        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0

        return cal.timeInMillis
    }

    private fun end(): Long {
        val cal = Calendar.getInstance()
        cal.time = Date()

        val end = cal.timeInMillis

        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0

        return end
    }


    override suspend fun signOut(): Flow<ResultWrapper<Response<Unit>>> {
        return safeApiCallFlow(call = {
            apiService.signOut()
        })
    }

    override suspend fun sendDataToServer(dataToServer: DataToServer): Flow<ResultWrapper<com.example.myapplication.data.remote.response.Response<DataFromServer>>> {
        return safeApiCallFlow(call = {
                apiService.sendDataToServer(dataToServer)
            }
        )
    }

    override fun subscribeDailySteps(listener: OnSuccessListener<DataSet>) {
        recordingClient
            .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("zzz", "Successfully subscribed steps!")
                    readDataDailySteps(listener)
                } else {
                    Log.w("zzz", "There was a problem subscribing steps.", task.exception)
                }
            }
    }

    override fun readDataDailySteps(listener: OnSuccessListener<DataSet>) {
        historyClient
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener(listener)
            .addOnFailureListener { e ->
                Log.w("zzz", "There was a problem getting the step count.", e)
            }
    }

    override fun subscribeDailyCaloriesAndDistance(listener: OnSuccessListener<Any>) {
        recordingClient.subscribe(DataType.TYPE_CALORIES_EXPENDED)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i("zzz", "Successfully subscribed calories!")
                    readDailyCaloriesAndDistance(listener)
                } else {
                    Log.w("zzz", "There was a problem subscribing calories.", it.exception)
                }
            }
    }

    override fun readDailyCaloriesAndDistance(listener: OnSuccessListener<Any>) {

        val readRequest: DataReadRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_CALORIES_EXPENDED)
            .aggregate(DataType.AGGREGATE_CALORIES_EXPENDED)
            .aggregate(DataType.TYPE_DISTANCE_DELTA)
            .aggregate(DataType.AGGREGATE_DISTANCE_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(start(), end(), TimeUnit.MILLISECONDS)
            .build()

        historyClient
            .readData(readRequest)
            .addOnSuccessListener(listener)
            .addOnFailureListener { e ->
                Log.w("zzz", "There was a problem getting the calories.", e)
            }
    }

    override fun subscribeWeightHeightAndHeartRate(listener: OnSuccessListener<Any>) {
        recordingClient.subscribe(DataType.TYPE_HEIGHT)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i("zzz", "Successfully subscribed height!")
                    readWeightHeightAndHeartRate(listener)
                } else {
                    Log.w("zzz", "There was a problem subscribing height.", it.exception)
                }
            }
    }

    override fun readWeightHeightAndHeartRate(listener: OnSuccessListener<Any>) {
        val readRequestBody: DataReadRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_HEIGHT)
            .read(DataType.TYPE_WEIGHT)
            .read(DataType.TYPE_HEART_RATE_BPM)
            .setTimeRange(1, end(), TimeUnit.MILLISECONDS)
            .setLimit(1)
            .build()

        historyClient.readData(readRequestBody)
            .addOnSuccessListener(listener)
            .addOnFailureListener { e ->
                Log.w("zzz", "There was a problem getting the height.", e)
            }
    }

    override fun subscribeBloodPressure(listener: OnSuccessListener<Any>) {
        recordingClient.subscribe(HealthDataTypes.TYPE_BLOOD_PRESSURE)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i("zzz", "Successfully subscribed BloodPressure!")
                    readBloodPressure(listener)
                } else {
                    Log.w("zzz", "There was a problem subscribing BloodPressure.", it.exception)
                }
            }
    }

    override fun readBloodPressure(listener: OnSuccessListener<Any>) {
        val readRequest: DataReadRequest = DataReadRequest.Builder()
            .aggregate(
                HealthDataTypes.TYPE_BLOOD_PRESSURE,
                HealthDataTypes.AGGREGATE_BLOOD_PRESSURE_SUMMARY
            )
            .bucketByTime(1, TimeUnit.DAYS)
            .enableServerQueries()
            .setTimeRange(start(), end(), TimeUnit.MILLISECONDS)
            .build()

        historyClient
            .readData(readRequest)
            .addOnSuccessListener(listener)
            .addOnFailureListener { e ->
                Log.w("zzz", "There was a problem getting the BloodPressure.", e)
            }
    }

    override fun subscribeBodyTemperature(listener: OnSuccessListener<Any>) {
        recordingClient.subscribe(HealthDataTypes.TYPE_BODY_TEMPERATURE)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i("zzz", "Successfully subscribed BodyTemperature!")
                    readBodyTemperature(listener)
                } else {
                    Log.w("zzz", "There was a problem subscribing BodyTemperature.", it.exception)
                }
            }
    }

    override fun readBodyTemperature(listener: OnSuccessListener<Any>) {
        val readRequest: DataReadRequest = DataReadRequest.Builder()
            .aggregate(
                HealthDataTypes.TYPE_BODY_TEMPERATURE,
                HealthDataTypes.AGGREGATE_BODY_TEMPERATURE_SUMMARY
            )
            .bucketByTime(1, TimeUnit.DAYS)
            .enableServerQueries()
            .setTimeRange(start(), end(), TimeUnit.MILLISECONDS)
            .build()

        historyClient
            .readData(readRequest)
            .addOnSuccessListener(listener)
            .addOnFailureListener { e ->
                Log.w("zzz", "There was a problem getting the BodyTemperature.", e)
            }
    }

}