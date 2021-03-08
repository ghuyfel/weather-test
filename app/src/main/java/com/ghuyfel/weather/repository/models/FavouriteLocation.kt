package com.ghuyfel.weather.repository.models

data class FavouriteLocation(
    val locationId: String,
    var locationName: String,
    val lat: Double,
    val lng: Double,
    var address: String?,
    var isCurrentLocation: Boolean = false
)
