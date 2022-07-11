package com.example.myapplication

data class FitnessDataResponseModel(
    var steps: Int? = 0,
    var calories: Float? = 0f,
    var distance: Float? = 0f,
    var heartRate: Int? = 0,
    var height: Float? = 0f,
    var weight: Float? = 0f,
    var bloodPressureSystolicAverage: Int? = 0,
    var bloodPressureSystolicMax: Int? = 0,
    var bloodPressureSystolicMin: Int? = 0,
    var bloodPressureDiastolicAverage: Int? = 0,
    var bloodPressureDiastolicMax: Int? = 0,
    var bloodPressureDiastolicMin: Int? = 0,
    var bodyTemperature: Float? = 0f
)
