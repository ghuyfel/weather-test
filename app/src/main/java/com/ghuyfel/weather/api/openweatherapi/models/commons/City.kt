package com.ghuyfel.weather.api.openweatherapi.models.commons

import androidx.annotation.Keep

@Keep
data class City(
    val coord: Coord,
    val country: String,
    val id: Int,
    val name: String
)