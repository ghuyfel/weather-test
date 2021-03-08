package com.ghuyfel.weather.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(@PrimaryKey(autoGenerate = false) val locationId: String, val name: String, val lat: Double, val lng: Double, val address: String?)
