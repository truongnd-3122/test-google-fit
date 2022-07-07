package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.OnSuccessListener
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit


//469986176513-dc3c9u04gbfbk0ic6bp45s4f872mf45m.apps.googleusercontent.com

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity(), OnSuccessListener<Any?>{
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

    private fun checkPermission(){
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
        }else{
            checkGoogleFitPermission()
        }
    }

    private fun checkGoogleFitPermission(){
        fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_READ)
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

    private fun getGoogleAccount(): GoogleSignInAccount = GoogleSignIn.getAccountForExtension(this@MainActivity, fitnessOptions)




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                checkGoogleFitPermission()
            }else{
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

    private fun requestForHistory(){
        val cal = Calendar.getInstance()
        cal.time = Date()
        val endTime = cal.timeInMillis

        cal[Calendar.HOUR_OF_DAY] = 0 //so it get all day and not the current hour

        cal[Calendar.MINUTE] = 0 //so it get all day and not the current minute

        cal[Calendar.SECOND] = 0 //so it get all day and not the current second

        val startTime = cal.timeInMillis

        val readRequest: DataReadRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA)
            .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
            .aggregate(DataType.TYPE_CALORIES_EXPENDED)
            .aggregate(DataType.AGGREGATE_CALORIES_EXPENDED)
            .aggregate(DataType.TYPE_DISTANCE_DELTA)
            .aggregate(DataType.AGGREGATE_DISTANCE_DELTA)
            .aggregate(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY)
//            .aggregate(DataType.AGGREGATE_HEART_RATE_SUMMARY)
//            .aggregate(DataType.TYPE_HEIGHT)
//            .aggregate(DataType.TYPE_WEIGHT)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(this@MainActivity, getGoogleAccount())
            .readData(readRequest)
            .addOnSuccessListener(this)
    }

    override fun onSuccess(o: Any?) {
        if (o is DataSet){
            val dataSet = o as DataSet
            if (dataSet!= null){
                getDataFromDataSet(dataSet)
            }
        }else if (o is DataReadResponse){
            fitnessDataResponseModel.steps = 0f
            fitnessDataResponseModel.calories = 0f
            fitnessDataResponseModel.distance = 0f
            fitnessDataResponseModel.heartRate = 0
            val dataReadResponse = o as DataReadResponse
            if (dataReadResponse.buckets != null && dataReadResponse.buckets.isNotEmpty()){
                val bucketList = dataReadResponse.buckets
                if (bucketList!= null && bucketList.isNotEmpty()){
                    for (bucket in bucketList){
                        val stepsDataSet = bucket.getDataSet(DataType.TYPE_STEP_COUNT_DELTA)
                        getDataFromDataReadResponse(stepsDataSet!!)
                        val caloriesDataSet = bucket.getDataSet(DataType.TYPE_CALORIES_EXPENDED)
                        getDataFromDataReadResponse(caloriesDataSet!!)
                        val distanceDataSet = bucket.getDataSet(DataType.TYPE_DISTANCE_DELTA)
                        getDataFromDataReadResponse(distanceDataSet!!)
                        val heartRateDataSet = bucket.getDataSet(DataType.TYPE_HEART_RATE_BPM)
                        getDataFromDataReadResponse(heartRateDataSet!!)
//                        val heightDataSet = bucket.getDataSet(DataType.TYPE_HEIGHT)
//                        getDataFromDataReadResponse(heightDataSet!!)
//                        val weightDataSet = bucket.getDataSet(DataType.TYPE_WEIGHT)
//                        getDataFromDataReadResponse(weightDataSet!!)
                    }
                }
            }
        }

    }

    private fun getDataFromDataSet(dataSet: DataSet) {
        val dataPoints = dataSet.dataPoints
        for (dataPoint in dataPoints) {
            Log.d("zzz", " data manual : " + dataPoint.originalDataSource.streamName)
            for (field in dataPoint.dataType.fields) {
                val value = dataPoint.getValue(field).toString().toFloat()
                Log.d("zzz", " data1 : $value")
                when (field.name) {
                    Field.FIELD_STEPS.name -> {
                        fitnessDataResponseModel.steps =
                            DecimalFormat("#.##").format(value.toDouble()).toFloat()
                    }
                    Field.FIELD_CALORIES.name -> {
                        fitnessDataResponseModel.calories =
                            DecimalFormat("#.##").format(value.toDouble()).toFloat()
                    }
                    Field.FIELD_DISTANCE.name -> {
                        fitnessDataResponseModel.distance =
                            DecimalFormat("#.##").format(value.toDouble()).toFloat()
                    }
                    Field.FIELD_BPM.name -> {
                        Log.d("zzz bpm", "bpm")
                        fitnessDataResponseModel.heartRate = value.toInt()
                    }
//                    Field.FIELD_HEIGHT.name -> {
//                        fitnessDataResponseModel.height =
//                            DecimalFormat("#.##").format(value.toDouble()).toFloat()
//                    }
//                    Field.FIELD_WEIGHT.name -> {
//                        fitnessDataResponseModel.weight =
//                            DecimalFormat("#.##").format(value.toDouble()).toFloat()
//                    }
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
                val value = dataPoint.getValue(field).toString().toFloat()
                Log.d("zzz", " data2 : $value")
                Log.d("zzz field", field.name)
                when (field.name) {
                    Field.FIELD_STEPS.name -> {
                        fitnessDataResponseModel.steps =
                            DecimalFormat("#.##").format(value + fitnessDataResponseModel.steps!!)
                                .toFloat()
                    }
                    Field.FIELD_CALORIES.name -> {
                        fitnessDataResponseModel.calories =
                            DecimalFormat("#.##").format(value + fitnessDataResponseModel.calories!!)
                                .toFloat()
                    }
                    Field.FIELD_DISTANCE.name -> {
                        fitnessDataResponseModel.distance =
                            DecimalFormat("#.##").format(value + fitnessDataResponseModel.distance!!)
                                .toFloat()
                    }
                    Field.FIELD_BPM.name -> {
                        Log.d("zzz bpm", "bpm")
                        fitnessDataResponseModel.heartRate = value.toInt()
                    }
//                    Field.FIELD_HEIGHT.name -> {
//                        fitnessDataResponseModel.height =
//                            DecimalFormat("#.##").format(value + fitnessDataResponseModel.height!!)
//                                .toFloat()
//                    }
//                    Field.FIELD_WEIGHT.name -> {
//                        fitnessDataResponseModel.weight =
//                            DecimalFormat("#.##").format(value + fitnessDataResponseModel.weight!!)
//                                .toFloat()
//                    }
                }
            }
        }
        Log.d("zzz fitness data2", fitnessDataResponseModel.toString())
//        binding.setFitnessData(fitnessDataResponseModel)
    }

}
