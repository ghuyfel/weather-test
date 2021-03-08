package com.ghuyfel.weather.ui.models

import androidx.annotation.Keep

@Keep
data class CurrentWeather(
    val currentTemp: Double,
    val minTemp: Double,
    val maxTemp: Double,
    val weather: String,
    val time: Long = System.currentTimeMillis(),
    val locationName: String = "",
    val locationId: String = ""
)
