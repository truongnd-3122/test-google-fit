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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import com.example.myapplication.BuildConfig
import com.example.myapplication.R
import com.example.myapplication.data.model.FitnessDataResponseModel
import com.example.myapplication.databinding.FragmentMainBinding
import com.example.myapplication.ui.base.BaseFragment
import com.example.myapplication.ui.screen.signin.SignInFragment
import com.example.myapplication.utils.PermissionStatus
import com.example.myapplication.utils.hideKeyboard
import com.example.myapplication.utils.requestPermissionLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.HealthDataTypes
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment: BaseFragment<FragmentMainBinding, MainViewModel>() {

    private lateinit var fitnessDataResponseModel: FitnessDataResponseModel

    private val fitnessOptions = FitnessOptions.builder()
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

    private val permissionsLauncher by requestPermissionLauncher { status ->
        when(status) {
            PermissionStatus.Granted -> {
                Log.i("zzz","Permission status: Granted")
                checkPermissionsAndRun()
            }
            PermissionStatus.Denied -> {
                Log.i("zzz","Permission status: Denied")
                requestRuntimePermissions(PermissionStatus.Denied)
            }
            PermissionStatus.ShowRationale -> {
                Log.i("zzz","Permission status: ShowRationale")
                requestRuntimePermissions(PermissionStatus.ShowRationale)
            }
        }
    }

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
        checkPermissionsAndRun()
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
        viewBinding.tvWelcome.text = getString(R.string.string_welcome_email) + "\n" + email
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (resultCode) {
            AppCompatActivity.RESULT_OK -> {
                Log.i("zzz", "QAuth sign in: RESULT_OK")
                getDataAndSendDataToServer()
            }
            else -> {
                oAuthErrorMsg(resultCode)
                showSnackBarError()
            }
        }
    }

    private fun oAuthErrorMsg( resultCode: Int) {
        val message = """
            There was an error signing into Fit. Check the troubleshooting section of the README
            for potential issues.
            Result code was: $resultCode
        """.trimIndent()
        Log.e("zzz", message)

    }

    private fun showSnackBarError(){
        Snackbar.make(
            viewBinding.root,
            R.string.qauth_error,
            Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.ok) {
                fitSignIn()
            }
            .show()
    }

    private fun fitSignIn() {
        if (oAuthPermissionsApproved()) {
            getDataAndSendDataToServer()
        } else {
            GOOGLE_FIT_PERMISSIONS_REQUEST_CODE.let {
                GoogleSignIn.requestPermissions(
                    this,
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    getGoogleAccount(), fitnessOptions)
            }
        }
    }

    private fun getGoogleAccount() = GoogleSignIn.getAccountForExtension(requireContext(), fitnessOptions)

    private fun checkPermissionsAndRun(){
        if (permissionApproved()) {
            fitSignIn()
        } else {
            requestRuntimePermissions(null)
        }
    }

    private fun permissionApproved(): Boolean {
        var approved = true
        for(i in getPermissionsArray())
        {
            if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(
                    requireContext(), i))
                approved =false
        }
        return approved
    }

    private fun requestRuntimePermissions(permissionStatus: PermissionStatus?) {
        when(permissionStatus)
        {
            null ->{
                var shouldProvideRationale = false
                for (i in getPermissionsArray()) {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), i))
                        shouldProvideRationale = true
                }
                if (shouldProvideRationale)
                    PermissionStatus.ShowRationale
                permissionsLauncher.launch(getPermissionsArray())
            }
            PermissionStatus.ShowRationale -> {
                Snackbar.make(
                    viewBinding.root,
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok) {
                        // Request permission
                        permissionsLauncher.launch(getPermissionsArray())
                    }
                    .show()
            }
            PermissionStatus.Denied ->{
                Snackbar.make(
                    viewBinding.root,
                    R.string.permission_denied_explanation,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.settings) {
                        // Build intent that displays the App settings screen.
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package",
                            BuildConfig.APPLICATION_ID, null)
                        intent.data = uri
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                    .show()
            }
            PermissionStatus.Granted ->{
                checkPermissionsAndRun()
            }
        }
    }

    private fun getPermissionsArray(): Array<String>
    {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            arrayOf(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    private fun oAuthPermissionsApproved() = GoogleSignIn.hasPermissions(getGoogleAccount(), fitnessOptions)

    private fun getDataAndSendDataToServer() {
        viewModel.sendDataToServer()
    }
}
