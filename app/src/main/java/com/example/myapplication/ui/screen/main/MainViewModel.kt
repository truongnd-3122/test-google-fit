package com.example.myapplication.ui.screen.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.pref.AppPrefs
import com.example.myapplication.data.repository.FitRepository
import com.example.myapplication.data.repository.MainRepository
import com.example.myapplication.ui.base.BaseViewModel
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.tasks.OnSuccessListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val appPrefs: AppPrefs,
    val repository: FitRepository
) : BaseViewModel() {

    val isLogout = MutableLiveData<Boolean>(false)
    val errorLiveData = MutableLiveData<String>()
    val emailLiveData = MutableLiveData<String>()

    val steps: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    private var currentSteps = 0
    fun subscribe(){
        val listener = OnSuccessListener<DataSet> { dataSet->
            val total = when {
                dataSet.isEmpty -> 0
                else -> dataSet.dataPoints.first().getValue(Field.FIELD_STEPS).asInt()
            }
            Log.i("TAG", "Total steps: $total")
            currentSteps = total
            steps.postValue(total)
        }
        repository.subscribe(listener)
    }

    fun signOut() {
        viewModelScope.launch {
            mainRepository.signOut().handleResponse(onSuccess = {
                Log.d("zzz mainviewmodel", "mainviewmodel")
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

    fun getToken(): String{
        val token = appPrefs.getAccessToken().toString()
        Log.d("zzz mainviewmodel", token)
        return token
    }
}
