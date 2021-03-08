package com.ghuyfel.weather.api.openweatherapi.models.commons

import androidx.annotation.Keep

@Keep
data class Forecast(
    val clouds: Clouds,
    val dt: Long,
    val dt_txt: String,
    val main: Main,
    val rain: Rain,
    val sys: Sys,
    val weather: List<Weather>,
    val wind: Wind
)