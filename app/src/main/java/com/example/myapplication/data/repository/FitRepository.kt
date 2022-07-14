package com.example.myapplication.data.repository

import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.tasks.OnSuccessListener

interface FitRepository {
    fun subscribe(listener: OnSuccessListener<DataSet>)
    fun readData(listener: OnSuccessListener<DataSet>)
//    fun setDataPointListener(listener: OnDataPointListener)
}