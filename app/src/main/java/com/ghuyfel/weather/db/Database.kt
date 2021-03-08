package com.ghuyfel.weather.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ghuyfel.weather.db.dao.LocationDao
import com.ghuyfel.weather.db.dao.WeatherDao
import com.ghuyfel.weather.db.entities.CurrentWeatherEntity
import com.ghuyfel.weather.db.entities.ForecastWeatherEntity
import com.ghuyfel.weather.db.entities.LocationEntity

@Database(entities = [LocationEntity::class, ForecastWeatherEntity::class, CurrentWeatherEntity::class], version = 11)
abstract class Database: RoomDatabase() {
    abstract fun getLocationDao(): LocationDao

    abstract fun getWeatherDao(): WeatherDao

    companion object {
        val DB_NAME = "db"
    }
}