package com.example.myapplication.ui.screen.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.myapplication.BuildConfig
import com.example.myapplication.R
import com.example.myapplication.data.model.FitnessDataResponseModel
import com.example.myapplication.databinding.FragmentMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import com.example.myapplication.ui.base.BaseFragment
import com.example.myapplication.ui.screen.signin.SignInFragment
import com.example.myapplication.utils.PermissionStatus
import com.example.myapplication.utils.hideKeyboard
import com.example.myapplication.utils.requestPermissionLauncher
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

@AndroidEntryPoint
class MainFragment: BaseFragment<FragmentMainBinding, MainViewModel>()
    , OnSuccessListener<Any?>, OnFailureListener
{

    private lateinit var fitnessOptions: FitnessOptions
    private lateinit var fitnessDataResponseModel: FitnessDataResponseModel

    companion object {
        fun newInstance() = MainFragment()

        const val MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 0
        const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1
    }


    override val layoutId: Int = R.layout.fragment_main

    override val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.getEmailUser()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.mainFragmentListener = this@MainFragment
        initControls()
        initListeners()
    }

    private fun initControls() {
        viewModel.getToken()
        fitnessDataResponseModel = FitnessDataResponseModel()
    }

    private fun initListeners() {
        viewModel.isLogout.observe(viewLifecycleOwner, this::handleLogout)
        viewModel.errorLiveData.observe(viewLifecycleOwner, this::handleError)
        viewModel.emailLiveData.observe(viewLifecycleOwner, this::handleEmailUser)
    }

    fun viewParentClick() {
        viewBinding.viewParent.hideKeyboard()
    }

    fun btnSendDataClick() {
        checkPermission()
    }

    fun btnSignOutClick() {
        viewModel.signOut()
    }

    private fun handleLogout(isLogout: Boolean){
        if (isLogout){
            GoogleSignIn.getLastSignedInAccount(requireActivity())
                ?.let { Fitness.getConfigClient(requireActivity(), it).disableFit() }
            handleLoading(false)
            replaceFragment(SignInFragment.newInstance(), R.id.container, false)
        }
    }

    private fun handleError(error: String){
        handleLoading(false)
    }

    private fun handleEmailUser(email: String){
        viewBinding.tvWelcome.text = getString(R.string.string_welcome_email) + email
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION
            )
        } else {
            Log.d("ppp","ppp1")
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
        Log.d("ppp acc", account.toString())

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                requireActivity(),
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                account,
                fitnessOptions
            )
            Log.d("ppp","ppp2")
        } else {
            requestForHistory()
            Log.d("ppp","ppp3")
        }
    }

    private fun getGoogleAccount(): GoogleSignInAccount =
        GoogleSignIn
            .getAccountForExtension(activity!!, fitnessOptions)

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
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("ppp", "request code: $requestCode")
        Log.d("ppp", "result code: $resultCode")
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            requestForHistory()
            Log.d("zzz", "onActivityResult")
            Log.d("ppp","ppp4")
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

        Fitness.getHistoryClient(requireActivity(), getGoogleAccount())
            .readData(readRequest)
            .addOnSuccessListener(this)
            .addOnFailureListener(this)
    }

    private fun getDailySteps() {
        Fitness.getHistoryClient(requireActivity(), getGoogleAccount())
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { response ->
                val total = when {
                    response.isEmpty -> 0
                    else -> response.dataPoints.first().getValue(Field.FIELD_STEPS).asInt()
                }
                fitnessDataResponseModel.steps = total
            }
//            .addOnFailureListener { e -> Log.i("eee", e.toString()) }
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


        Fitness.getHistoryClient(requireActivity(), getGoogleAccount())
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

        Fitness.getHistoryClient(requireActivity(), getGoogleAccount())
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

        Fitness.getHistoryClient(requireActivity(), getGoogleAccount())
            .readData(readRequest)
            .addOnSuccessListener(this)
            .addOnFailureListener(this)
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
