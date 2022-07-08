package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.HistoryApi
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit


//469986176513-dc3c9u04gbfbk0ic6bp45s4f872mf45m.apps.googleusercontent.com

class MainActivity : AppCompatActivity(), OnSuccessListener<Any?>, OnFailureListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fitnessOptions: FitnessOptions
    private lateinit var fitnessDataResponseModel: FitnessDataResponseModel

    companion object {
        const val MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 0
        const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        fitnessDataResponseModel = FitnessDataResponseModel()

        checkPermission()

        binding.btn.setOnClickListener {
            requestForHistory()
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
//            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
//            .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_HEIGHT_SUMMARY, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_WEIGHT_SUMMARY, FitnessOptions.ACCESS_READ)
            .build()

        val account: GoogleSignInAccount = getGoogleAccount()

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this@MainActivity,
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                account,
                fitnessOptions
            )
        } else {
            Log.d("zzz", "start reading")
        }
    }

    private fun getGoogleAccount(): GoogleSignInAccount =
        GoogleSignIn.getAccountForExtension(this@MainActivity, fitnessOptions)


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
        if (resultCode == RESULT_OK && requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
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
//
        getDailySteps()

        getHeightAndWeight(startTime, endTime)


    }

    private fun getHeightAndWeight(startTime: Long, endTime: Long) {
        val readRequestBody: DataReadRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_HEIGHT)
//            .read(DataType.TYPE_WEIGHT)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .setLimit(1)
            .build()


        Fitness.getHistoryClient(this@MainActivity, getGoogleAccount())
            .readData(readRequestBody)
            .addOnSuccessListener { response ->
//                onSuccess(response)
                if (response is DataReadResponse) {
                    if (response.buckets != null && response.buckets.isNotEmpty()) {
                        Log.d("qqq", "if")
                        for (bucket in response.buckets) {
//                            val caloriesDataSet = bucket.getDataSet(DataType.TYPE_CALORIES_EXPENDED)
//                            getDataFromDataReadResponse(caloriesDataSet!!)
//                            val distanceDataSet = bucket.getDataSet(DataType.TYPE_DISTANCE_DELTA)
//                            getDataFromDataReadResponse(distanceDataSet!!)
                        }

                    } else if (response.dataSets != null && response.dataSets.isNotEmpty()) {
                        Log.d("qqq", "else" + response.dataSets.size.toString())
                        for (dataSet in response.dataSets){
//                            getDataFromDataReadResponse(dataSet)
                            Log.d("qqq", dataSet.dataPoints.size.toString())
//                            dataSet.
                        }

                    }
                }
            }
            .addOnFailureListener { e -> Log.i("zzz0", e.toString()) }

    }


    private fun getCaloriesAndDistance(startTime: Long, endTime: Long) {
        val readRequest: DataReadRequest = DataReadRequest.Builder()
//            .aggregate(DataType.TYPE_STEP_COUNT_DELTA)
//            .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
            .aggregate(DataType.TYPE_CALORIES_EXPENDED)
            .aggregate(DataType.AGGREGATE_CALORIES_EXPENDED)
            .aggregate(DataType.TYPE_DISTANCE_DELTA)
            .aggregate(DataType.AGGREGATE_DISTANCE_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(this@MainActivity, getGoogleAccount())
            .readData(readRequest)
            .addOnSuccessListener(this)
    }

    private fun getDailySteps() {
        Fitness.getHistoryClient(this@MainActivity, getGoogleAccount())
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { response ->
                val total = when {
                    response.isEmpty -> 0
                    else -> response.dataPoints.first().getValue(Field.FIELD_STEPS).asInt()
                }
                fitnessDataResponseModel.steps = total
            }
            .addOnFailureListener { e -> Log.i("zzz0", e.toString()) }
    }

    override fun onSuccess(o: Any?) {
        if (o is DataSet) {
            if (o != null) {
                getDataFromDataSet(o)
            }
        } else if (o is DataReadResponse) {
            fitnessDataResponseModel.calories = 0f
            fitnessDataResponseModel.distance = 0f
            if (o.buckets != null && o.buckets.isNotEmpty()) {
                for (bucket in o.buckets) {
                    val caloriesDataSet = bucket.getDataSet(DataType.TYPE_CALORIES_EXPENDED)
                    getDataFromDataReadResponse(caloriesDataSet!!)
                    val distanceDataSet = bucket.getDataSet(DataType.TYPE_DISTANCE_DELTA)
                    getDataFromDataReadResponse(distanceDataSet!!)
                }

            } else if (o.dataSets != null && o.dataSets.isNotEmpty()) {
                for (dataSet in o.dataSets){
                    getDataFromDataReadResponse(dataSet)
                }

            }
        }

    }

    private fun getDataFromDataSet(dataSet: DataSet) {
        val dataPoints = dataSet.dataPoints
        for (dataPoint in dataPoints) {
            Log.d("zzz1", " data manual : " + dataPoint.originalDataSource.streamName)
            for (field in dataPoint.dataType.fields) {
                Log.d("zzz1", "field $field")
                val value = dataPoint.getValue(field).toString().toFloat()
                Log.d("zzz1", " data1 : $value")
                when (field.name) {
                    Field.FIELD_STEPS.name -> {
                        fitnessDataResponseModel.steps = value.toInt()
                    }
                    Field.FIELD_CALORIES.name -> {
                        fitnessDataResponseModel.calories =
                            DecimalFormat("#.##").format(value.toDouble()).toFloat()
                    }
                    Field.FIELD_DISTANCE.name -> {
                        fitnessDataResponseModel.distance =
                            DecimalFormat("#.##").format(value.toDouble()).toFloat()
                    }
                }
            }
        }
        Log.d("zzz fitness data1", fitnessDataResponseModel.toString())
//        activityMainBinding.setFitnessData(fitnessDataResponseModel)
    }

    private fun getDataFromDataReadResponse(dataSet: DataSet) {
        val dataPoints = dataSet.dataPoints
        for (dataPoint in dataPoints) {
            for (field in dataPoint.dataType.fields) {
                Log.d("zzz field", field.name)
                val value = dataPoint.getValue(field).toString().toFloat()
                when (field.name) {
                    Field.FIELD_STEPS.name -> {
                        fitnessDataResponseModel.steps = value.toInt()
                    }
                    Field.FIELD_CALORIES.name -> {
                        fitnessDataResponseModel.calories = DecimalFormat("#.##").format(value).toFloat()
                    }
                    Field.FIELD_DISTANCE.name -> {
                        fitnessDataResponseModel.distance = DecimalFormat("#.##").format(value).toFloat()
                    }

                    Field.FIELD_HEIGHT.name -> {
                        Log.d("zzz height", value.toString())
                        fitnessDataResponseModel.height = DecimalFormat("#.##").format(value).toFloat()
                    }
                    Field.FIELD_WEIGHT.name -> {
                        Log.d("zzz weight", value.toString())
                        fitnessDataResponseModel.weight = DecimalFormat("#.##").format(value).toFloat()
                    }
                }
            }
        }
        Log.d("zzz fitness data2", fitnessDataResponseModel.toString())

    }

    override fun onFailure(e: Exception) {
        Log.d("zzz", e.toString())
    }

}
