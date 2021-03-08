package com.ghuyfel.weather.api.openweatherapi.models.commons

import androidx.annotation.Keep

@Keep
data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)