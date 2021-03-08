package com.ghuyfel.weather.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecast_weather")
data class ForecastWeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val locationId: String,
    val locationName: String,
    val dayOfTheWeek: String,
    val weather: String,
    val temp: Double,
    val time: Long
)