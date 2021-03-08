package com.ghuyfel.weather.ui.models

import androidx.annotation.Keep

@Keep
data class ForecastWeather(
    val dayOfTheWeek: String,
    val weather: String,
    val temp: Double,
    val time: Long,
    val locationId: String = "",
    val locationName: String = ""
)
