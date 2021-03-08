package com.ghuyfel.weather.repository.models

import androidx.annotation.Keep
import com.ghuyfel.weather.ui.models.CurrentWeather
import com.ghuyfel.weather.ui.models.ForecastWeather
import com.ghuyfel.weather.utils.DataState

@Keep
data class WeatherBulletin(
    val currentWeather: DataState<CurrentWeather>,
    val forecastWeather: DataState<List<ForecastWeather>>,
    var timestamp: Long = System.currentTimeMillis()
)
