package com.ghuyfel.weather.utils

import com.ghuyfel.weather.R
import java.util.*

object ResourceUtils {
    fun getIconForWeatherCondition(weather: String): Int =
        when (weather.toLowerCase(Locale.getDefault())) {
            "clear" -> R.drawable.clear
            "clouds" -> R.drawable.cloudy
            "rain" -> R.drawable.rainy
            "snow" -> R.drawable.snow
            else -> R.drawable.ic_error
        }

    fun getTextForWeatherCondition(weather: String): Int =
        when (weather.toLowerCase(Locale.getDefault())) {
            "clear" -> R.string.sunny
            "clouds" -> R.string.cloudy
            "rain" -> R.string.rainy
            "snow" -> R.string.snow
            else -> -1
        }

    fun getBackgroundDrawableForWeatherCondition(weather: String): Int =
        when (weather.toLowerCase(Locale.getDefault())) {
            "clear" -> R.drawable.forest_sunny
            "clouds" -> R.drawable.forest_cloudy
            "rain" -> R.drawable.forest_rainy
            "snow" -> R.drawable.forest_rainy
            else -> android.R.color.transparent
        }

    fun getColourForWeatherCondition(weather: String): Int =
        when (weather.toLowerCase(Locale.getDefault())) {
            "clear" -> R.color.sunny
            "clouds" -> R.color.cloudy
            "rain" -> R.color.rainy
            "snow" -> R.color.cloudy
            else -> android.R.color.transparent        }
}