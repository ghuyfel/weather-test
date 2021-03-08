package com.ghuyfel.weather.api.openweatherapi.models.params

import androidx.annotation.Keep

@Keep
data class WeatherRequestParams(
    val lng: Double,
    val lat: Double,
    val locationName: String = "",
    val locationId: String = ""
)
