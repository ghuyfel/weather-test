package com.ghuyfel.weather.ui.interfaces

import com.ghuyfel.weather.repository.models.FavouriteLocation


interface FavouriteLocationClickListener {
    fun onFavouriteLocationClicked(location: FavouriteLocation)
    fun onDeleteButtonClicked(location: FavouriteLocation)
}