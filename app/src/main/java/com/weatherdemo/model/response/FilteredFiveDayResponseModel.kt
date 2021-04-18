package com.weatherdemo.model.response

data class FilteredFiveDayResponseModel(
    val date: String = "",
    val day: String = "",
    val humidity: Int = 0,
    val wind: Double = 0.0,
    val rain: Int = 0,
    val minTemp: Double = 0.0,
    val maxTemp: Double = 0.0,

)