package com.ghuyfel.weather.ui.events

import com.ghuyfel.weather.repository.models.FavouriteLocation
import com.google.android.libraries.places.api.model.Place

sealed class FavouritePlacesEvents {
    object GetFavouritePlacesLocations : FavouritePlacesEvents()
    data class LocationClicked(val favouriteLocation: FavouriteLocation) : FavouritePlacesEvents()
    data class SaveLocation(val place: Place) : FavouritePlacesEvents()
    data class DeleteLocation(val favouriteLocation: FavouriteLocation) : FavouritePlacesEvents()
    object GetCurrentFusedLocation: FavouritePlacesEvents()

    object GetCurrentLocation : FavouritePlacesEvents()
}