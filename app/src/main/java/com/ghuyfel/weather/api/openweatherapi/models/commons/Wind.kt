package com.ghuyfel.weather.api.openweatherapi.models.commons

import androidx.annotation.Keep

@Keep
data class Wind(
    val deg: Double,
    val speed: Double
)