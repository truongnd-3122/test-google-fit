package com.example.myapplication.ui.screen.main

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.data.local.pref.AppPrefs
import com.example.myapplication.data.model.DataBloodPressure
import com.example.myapplication.data.model.DataTemperature
import com.example.myapplication.data.model.DataToServer
import com.example.myapplication.data.model.FitnessDataResponseModel
import com.example.myapplication.data.repository.MainRepository
import com.example.myapplication.ui.base.BaseViewModel
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.OnSuccessListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val appPrefs: AppPrefs,
    private val resources: Resources
) : BaseViewModel() {

    val isLogout = MutableLiveData(false)
    val errorLiveData = MutableLiveData<String>()
    val emailLiveData = MutableLiveData<String>()

    private val fitnessDataResponseModel = FitnessDataResponseModel()

    private fun subscribeDailySteps(){
        val listener = OnSuccessListener<DataSet> { dataSet->
            val total = when {
                dataSet.isEmpty -> 0
                else -> dataSet.dataPoints.first().getValue(Field.FIELD_STEPS).asInt()
            }
            Log.i("zzz", "Total steps: $total")
            fitnessDataResponseModel.steps = total
        }
        mainRepository.subscribeDailySteps(listener)
    }

    private fun subscribeDailyCaloriesAndDistance(){
        val listener = OnSuccessListener<Any>{ a ->
            if (a is DataSet){
                if (a != null) {
                    getDataFromDataReadResponse(a)
                }
            }else if (a is DataReadResponse){
                if (a.buckets != null && a.buckets.isNotEmpty()) {
                    for (bucket in a.buckets) {
                        val caloriesDataSet = bucket.getDataSet(DataType.TYPE_CALORIES_EXPENDED)
                        caloriesDataSet?.let { getDataFromDataReadResponse(it) }
                        val distanceDataSet = bucket.getDataSet(DataType.TYPE_DISTANCE_DELTA)
                        distanceDataSet?.let { getDataFromDataReadResponse(it) }
                        val bloodPressure =
                            bucket.getDataSet(HealthDataTypes.AGGREGATE_BLOOD_PRESSURE_SUMMARY)
                        bloodPressure?.let { getDataFromDataReadResponse(it) }
                        val bodyTemperature =
                            bucket.getDataSet(HealthDataTypes.AGGREGATE_BODY_TEMPERATURE_SUMMARY)
                        bodyTemperature?.let { getDataFromDataReadResponse(it) }
                    }

                } else if (a.dataSets != null && a.dataSets.isNotEmpty()) {
                    for (dataSet in a.dataSets) {
                        getDataFromDataReadResponse(dataSet)
                    }
                }
            }

        }
        mainRepository.subscribeDailyCaloriesAndDistance(listener)
        mainRepository.subscribeWeightHeightAndHeartRate(listener)
        mainRepository.subscribeBloodPressure(listener)
        mainRepository.subscribeBodyTemperature(listener)
    }

    private fun getDataFromDataReadResponse(dataSet: DataSet) {
        val dataPoints = dataSet.dataPoints
        for (dataPoint in dataPoints) {
            for (field in dataPoint.dataType.fields) {
                Log.d("zzz field", "${field.name} - value: ${dataPoint.getValue(field)}")

                var value: Number = 0

                if (Pattern.matches(".*\\d.*", dataPoint.getValue(field).toString())) {
                    value = dataPoint.getValue(field).toString().toFloat()
                }


                when (field.name) {
                    Field.FIELD_STEPS.name -> {
                        fitnessDataResponseModel.steps = value.toInt()
                    }
                    Field.FIELD_CALORIES.name -> {
                        fitnessDataResponseModel.calories =
                            DecimalFormat("#.##").format(value).toFloat()
                    }
                    Field.FIELD_DISTANCE.name -> {
                        fitnessDataResponseModel.distance =
                            DecimalFormat("#.##").format(value).toFloat()
                    }
                    Field.FIELD_HEIGHT.name -> {
                        fitnessDataResponseModel.height =
                            DecimalFormat("#.##").format(value).toFloat()
                    }
                    Field.FIELD_WEIGHT.name -> {
                        fitnessDataResponseModel.weight =
                            DecimalFormat("#.##").format(value).toFloat()
                    }
                    Field.FIELD_BPM.name -> {
                        fitnessDataResponseModel.heartRate = value.toInt()
                    }
                    HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC_AVERAGE.name -> {
                        fitnessDataResponseModel.bloodPressureSystolicAverage = value.toInt()
                    }
                    HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC_MAX.name -> {
                        fitnessDataResponseModel.bloodPressureSystolicMax = value.toInt()
                    }
                    HealthFields.FIELD_BLOOD_PRESSURE_SYSTOLIC_MIN.name -> {
                        fitnessDataResponseModel.bloodPressureSystolicMin = value.toInt()
                    }
                    HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC_AVERAGE.name -> {
                        fitnessDataResponseModel.bloodPressureDiastolicAverage = value.toInt()
                    }
                    HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC_MAX.name -> {
                        fitnessDataResponseModel.bloodPressureDiastolicMax = value.toInt()
                    }
                    HealthFields.FIELD_BLOOD_PRESSURE_DIASTOLIC_MIN.name -> {
                        fitnessDataResponseModel.bloodPressureDiastolicMin = value.toInt()
                    }
                    Field.FIELD_AVERAGE.name -> {
                        fitnessDataResponseModel.bodyTemperature =
                            DecimalFormat("#.##").format(value).toFloat()
                    }
                }
            }
        }
        Log.d("zzz", fitnessDataResponseModel.toString())

    }

    fun signOut() {
        viewModelScope.launch {
            mainRepository.signOut().handleResponse(onSuccess = {
                isLogout.value = true
                appPrefs.clear()
            }, onError = {
                isLogout.value = false
                errorLiveData.postValue(it?.errors?.fullMessage)
            })
        }
    }

    fun getEmailUser(){
        emailLiveData.value = appPrefs.getEmail()
    }

    fun getToken(): String = appPrefs.getAccessToken().toString()

    fun sendDataToServer(){
        subscribeDailySteps()
        subscribeDailyCaloriesAndDistance()
        viewModelScope.launch {
            val string = SimpleDateFormat(resources.getString(R.string.format_yyyy_MM_dd), Locale.getDefault()).format(
                Date()
            )
            val dataTemperature = DataTemperature(fitnessDataResponseModel.bodyTemperature!!, "celsius", "2022-06-20")
            val dataBloodPressure = DataBloodPressure(fitnessDataResponseModel.bloodPressureSystolicMin!!, fitnessDataResponseModel.bloodPressureSystolicMax!!)
            val dataToServer = DataToServer("2022-06-20", dataTemperature, dataBloodPressure)
            mainRepository.sendDataToServer(dataToServer).handleResponse(onSuccess = {

            }, onError = {

            })
        }
    }
}
