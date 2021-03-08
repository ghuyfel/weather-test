package com.ghuyfel.weather.maps.utils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

object MapUtils {
    fun moveCameraToCoordinates(map: GoogleMap, latLng: LatLng) {
        val cameraPosition = CameraPosition.Builder()
            .target(latLng) // Sets the center of the map to Mountain View
            .zoom(15f) // Sets the zoom
            .bearing(0f) // Sets the orientation of the camera to east
            .tilt(0f) // Sets the tilt of the camera to 30 degrees
            .build() // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    fun addMarker(map: GoogleMap, latLng: LatLng, name: String, colour: Boolean = false): Marker {
        val options = MarkerOptions().position(latLng)
            .title(name)
        if(colour) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        }
        return map.addMarker(options)
    }

    fun fitBoundsForCameraTarget(map: GoogleMap, coordinates: List<LatLng>) {
        val boundsBuilder = LatLngBounds.builder()
        for (latLng in coordinates)
            boundsBuilder.include(latLng)
        val bounds = boundsBuilder.build()
        val center = LatLng(bounds.center.latitude, bounds.center.longitude)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 0f))

    }
}