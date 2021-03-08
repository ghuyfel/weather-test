package com.ghuyfel.weather.api.openweatherapi.models.responses

import androidx.annotation.Keep
import com.ghuyfel.weather.api.openweatherapi.models.commons.City
import com.ghuyfel.weather.api.openweatherapi.models.commons.Forecast

@Keep
data class GetForecastWeatherResponse(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<Forecast>,
    val message: Double
)