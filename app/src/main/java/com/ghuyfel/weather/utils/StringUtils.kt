package com.ghuyfel.weather.utils

import kotlin.math.roundToInt

object StringUtils {
    fun formatTemperature(temp: Double): String =  String.format("%d%s",  temp.roundToInt(), "°")
}