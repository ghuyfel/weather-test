package com.ghuyfel.weather.db.dao

import androidx.room.*
import com.ghuyfel.weather.db.entities.CurrentWeatherEntity
import com.ghuyfel.weather.db.entities.ForecastWeatherEntity

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(forecast: ForecastWeatherEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currentForecast: CurrentWeatherEntity): Long

    @Query("DELETE FROM forecast_weather WHERE locationName = :locationName")
    suspend fun deleteAllForecastsForLocationId(locationName: String)

    @Query("SELECT * FROM forecast_weather WHERE locationName = :locationName")
    suspend fun getForecastsForLocation(locationName: String): List<ForecastWeatherEntity>

    @Query("SELECT * FROM current_weather WHERE locationName = :locationName LIMIT 1")
    suspend fun getCurrentWeather(locationName: String): CurrentWeatherEntity?
}