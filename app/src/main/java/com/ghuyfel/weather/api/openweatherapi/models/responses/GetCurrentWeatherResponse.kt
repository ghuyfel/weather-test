package com.ghuyfel.weather.api.openweatherapi.models.responses

import androidx.annotation.Keep
import com.ghuyfel.weather.api.openweatherapi.models.commons.*

@Keep
data class GetCurrentWeatherResponse(
    val base: String,
    val clouds: Clouds,
    val cod: Int,
    val coord: Coord,
    val dt: Int,
    val id: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: Int,
    val weather: List<Weather>,
    val wind: Wind
)