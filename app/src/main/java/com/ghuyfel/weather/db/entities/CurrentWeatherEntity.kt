package com.ghuyfel.weather.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_weather")
data class CurrentWeatherEntity(
    @PrimaryKey(autoGenerate = false)
    val locationName: String,
    val locationId: String,
    val currentTemp: Double,
    val minTemp: Double,
    val maxTemp: Double,
    val weather: String,
    val time: Long = System.currentTimeMillis()
)