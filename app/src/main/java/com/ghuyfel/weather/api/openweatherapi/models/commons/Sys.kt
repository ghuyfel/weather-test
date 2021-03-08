package com.ghuyfel.weather.api.openweatherapi.models.commons

import androidx.annotation.Keep

@Keep
data class Sys(
    val country: String,
    val id: Int,
    val message: Double,
    val sunrise: Int,
    val sunset: Int,
    val type: Int
)