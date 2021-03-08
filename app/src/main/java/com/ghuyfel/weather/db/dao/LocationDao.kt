package com.ghuyfel.weather.db.dao

import androidx.room.*
import com.ghuyfel.weather.db.entities.LocationEntity

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: LocationEntity): Long

    @Delete
    suspend fun delete(location: LocationEntity)

    @Query("SELECT * FROM locations ORDER BY name ASC")
    suspend fun getAllLocations(): List<LocationEntity>

    @Query("SELECT * FROM locations WHERE locationId = :locationId")
    suspend fun getLocation(locationId: String): LocationEntity
}