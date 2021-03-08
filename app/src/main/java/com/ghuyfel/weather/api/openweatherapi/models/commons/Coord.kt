package com.ghuyfel.weather.api.openweatherapi.models.commons

import androidx.annotation.Keep

@Keep
data class Coord(
    val lat: Double,
    val lon: Double
)