package com.example.myapplication.ui.screen.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.myapplication.R
import com.example.myapplication.data.model.FitnessDataResponseModel
import com.example.myapplication.databinding.ActivityHomeBinding
import com.example.myapplication.ui.base.BaseActivity
import com.example.myapplication.ui.screen.main.MainFragment.Companion.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE
import com.example.myapplication.ui.screen.main.MainFragment.Companion.MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(), OnSuccessListener<Any?>, OnFailureListener {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var fitnessOptions: FitnessOptions
    private lateinit var fitnessDataResponseModel: FitnessDataResponseModel

    companion object {
        const val MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 0
        const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1
    }

    override val viewModel: HomeViewModel by viewModels()

    override val layoutId: Int = R.layout.activity_home


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        fitnessDataResponseModel = FitnessDataResponseModel()


        binding.btn.setOnClickListener {
            checkPermission()
        }

        binding.btn1.setOnClickListener {
            writeBodyTemperature()
        }


    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION
            )
        } else {
            checkGoogleFitPermission()
        }
    }

    private fun checkGoogleFitPermission() {
        fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_HEIGHT_SUMMARY, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_WEIGHT_SUMMARY, FitnessOptions.ACCESS_READ)
            .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
            .addDataType(HealthDataTypes.AGGREGATE_BLOOD_PRESSURE_SUMMARY, FitnessOptions.ACCESS_READ)
            .addDataType(HealthDataTypes.TYPE_BODY_TEMPERATURE, FitnessOptions.ACCESS_READ)
            .addDataType(HealthDataTypes.TYPE_BODY_TEMPERATURE, FitnessOptions.ACCESS_WRITE)
            .build()

        val account: GoogleSignInAccount = getGoogleAccount()

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this@HomeActivity,
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                account,
                fitnessOptions
            )
        } else {
            requestForHistory()
        }
    }

    private fun getGoogleAccount(): GoogleSignInAccount =
        GoogleSignIn.getAccountForExtension(this@HomeActivity, fitnessOptions)


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkGoogleFitPermission()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("zzz", "request: $requestCode")
        Log.d("zzz", "result: $resultCode")
        if (resultCode == RESULT_OK && requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            Log.d("zzz", GoogleSignIn.getLastSignedInAccount(this).toString())
            requestForHistory()
            Log.d("zzz", "onActivityResult")
        }
    }

    private fun requestForHistory() {
        val cal = Calendar.getInstance()
        cal.time = Date()

        val endTime = cal.timeInMillis

        cal[Calendar.HOUR_OF_DAY] = 0 //so it get all day and not the current hour
        cal[Calendar.MINUTE] = 0 //so it get all day and not the current minute
        cal[Calendar.SECOND] = 0 //so it get all day and not the current second

        val startTime = cal.timeInMillis

        getCaloriesAndDistance(startTime, endTime)

        getDailySteps()

        getHeightWeightAndHeartRate(endTime)

        getBloodPressure(startTime, endTime)

        getBodyTemperature(startTime, endTime)
    }


    private fun getCaloriesAndDistance(startTime: Long, endTime: Long) {
        val readRequest: DataReadRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_CALORIES_EXPENDED)
            .aggregate(DataType.AGGREGATE_CALORIES_EXPENDED)
            .aggregate(DataType.TYPE_DISTANCE_DELTA)
            .aggregate(DataType.AGGREGATE_DISTANCE_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(this@HomeActivity, getGoogleAccount())
            .readData(readRequest)
            .addOnSuccessListener(this)
            .addOnFailureListener(this)
    }

    private fun getDailySteps() {
        Fitness.getHistoryClient(this@HomeActivity, getGoogleAccount())
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { response ->
                val total = when {
                    response.isEmpty -> 0
                    else -> response.dataPoints.first().getValue(Field.FIELD_STEPS).asInt()
                }
                fitnessDataResponseModel.steps = total
            }
            .addOnFailureListener { e -> Log.i("zzz0", e.toString()) }
            .addOnFailureListener(this)
    }

    private fun getHeightWeightAndHeartRate(endTime: Long) {
        val readRequestBody: DataReadRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_HEIGHT)
            .read(DataType.TYPE_WEIGHT)
            .read(DataType.TYPE_HEART_RATE_BPM)
            .setTimeRange(1, endTime, TimeUnit.MILLISECONDS)
            .setLimit(1)
            .build()


        Fitness.getHistoryClient(this@HomeActivity, getGoogleAccount())
            .readData(readRequestBody)
            .addOnSuccessListener(this)
            .addOnFailureListener(this)

    }

    private fun getBloodPressure(startTime: Long, endTime: Long) {
        val readRequest: DataReadRequest = DataReadRequest.Builder()
            .aggregate(
                HealthDataTypes.TYPE_BLOOD_PRESSURE,
                HealthDataTypes.AGGREGATE_BLOOD_PRESSURE_SUMMARY
            )
            .bucketByTime(1, TimeUnit.DAYS)
            .enableServerQueries()
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(this@HomeActivity, getGoogleAccount())
            .readData(readRequest)
            .addOnSuccessListener(this)
            .addOnFailureListener(this)
    }

    private fun getBodyTemperature(startTime: Long, endTime: Long) {
        val readRequest: DataReadRequest = DataReadRequest.Builder()
            .aggregate(
                HealthDataTypes.TYPE_BODY_TEMPERATURE,
                HealthDataTypes.AGGREGATE_BODY_TEMPERATURE_SUMMARY
            )
            .bucketByTime(1, TimeUnit.DAYS)
            .enableServerQueries()
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(this@HomeActivity, getGoogleAccount())
            .readData(readRequest)
            .addOnSuccessListener(this)
//            .addOnSuccessListener {
//                for (dataSet in it.buckets.flatMap { it.dataSets }) {
//                    dumpDataSet(dataSet)
//                }
//            }
            .addOnFailureListener(this)
    }

    private fun dumpDataSet(dataSet: DataSet) {
        for (dp in dataSet.dataPoints) {
            for (field in dp.dataType.fields) {
                when(field){
                    Field.FIELD_AVERAGE -> {
                        fitnessDataResponseModel.bodyTemperature =
                            DecimalFormat("#.##").format(dp.getValue(Field.FIELD_AVERAGE).asFloat()).toFloat()
                    }
                }
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun writeBodyTemperature() {
        val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val startTime = endTime.minusHours(1)
        val dataSource = DataSource.Builder()
            .setAppPackageName(this)
            .setDataType(HealthDataTypes.TYPE_BODY_TEMPERATURE)
            .setStreamName("body temperature")
            .setType(DataSource.TYPE_RAW)
            .build()

        val bodyTemp = 36.5f
        val dataPoint = DataPoint.builder(dataSource)
            .setField(HealthFields.FIELD_BODY_TEMPERATURE, bodyTemp)
            .setTimeInterval(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
            .build()

        val dataSet = DataSet.builder(dataSource)
            .add(dataPoint)
            .build()

        Fitness.getHistoryClient(this@HomeActivity, getGoogleAccount())
            .insertData(dataSet)
            .addOnSuccessListener {
                Log.i("zzz", "DataSet added successfully!")
            }
            .addOnFailureListener { e ->
                Log.w("zzz", "There was an error adding the DataSet", e)
            }
    }

    private fun getDataFromDataReadResponse(dataSet: DataSet) {
        val dataPoints = dataSet.dataPoints
        for (dataPoint in dataPoints) {
            for (field in dataPoint.dataType.fields) {
                Log.d("zzz field", "${field.name} - value: ${dataPoint.getValue(field)}")

                var value: Number = 0

                if (Pattern.matches(".*\\d.*", dataPoint.getValue(field).toString())){
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
        Log.d("zzz fitness data2", fitnessDataResponseModel.toString())

    }

    override fun onSuccess(o: Any?) {
        if (o is DataSet) {
            if (o != null) {
                getDataFromDataReadResponse(o)
            }
        } else if (o is DataReadResponse) {
            if (o.buckets != null && o.buckets.isNotEmpty()) {
                for (bucket in o.buckets) {
                    val caloriesDataSet = bucket.getDataSet(DataType.TYPE_CALORIES_EXPENDED)
                    caloriesDataSet?.let { getDataFromDataReadResponse(it) }
                    val distanceDataSet = bucket.getDataSet(DataType.TYPE_DISTANCE_DELTA)
                    distanceDataSet?.let { getDataFromDataReadResponse(it) }
                    val bloodPressure = bucket.getDataSet(HealthDataTypes.AGGREGATE_BLOOD_PRESSURE_SUMMARY)
                    bloodPressure?.let { getDataFromDataReadResponse(it) }
                    val bodyTemperature = bucket.getDataSet(HealthDataTypes.AGGREGATE_BODY_TEMPERATURE_SUMMARY)
                    bodyTemperature?.let { getDataFromDataReadResponse(it) }
                }

            } else if (o.dataSets != null && o.dataSets.isNotEmpty()) {
                for (dataSet in o.dataSets) {
                    getDataFromDataReadResponse(dataSet)
                }

            }
        }

    }

    override fun onFailure(e: Exception) {
        Log.d("eee", e.toString())
    }

}
