package com.ghuyfel.weather.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghuyfel.weather.api.openweatherapi.models.params.WeatherRequestParams
import com.ghuyfel.weather.repository.Repository
import com.ghuyfel.weather.repository.models.FavouriteLocation
import com.ghuyfel.weather.repository.models.WeatherBulletin
import com.ghuyfel.weather.ui.events.FavouritePlacesEvents
import com.ghuyfel.weather.ui.models.LocationPicture
import com.ghuyfel.weather.utils.DataState
import com.ghuyfel.weather.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class FavouritePlacesFragmentViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _favouriteLocationInsertedLiveData = MutableLiveData<DataState<FavouriteLocation>>()
    val favouriteLocationInsertedLiveData: LiveData<DataState<FavouriteLocation>>
        get() = _favouriteLocationInsertedLiveData

    private val _favouriteLocationsLiveData = MutableLiveData<DataState<List<FavouriteLocation>>>()
    val favouriteLocationsLiveData: LiveData<DataState<List<FavouriteLocation>>>
        get() = _favouriteLocationsLiveData

    private val _deleteFavouriteLocationLiveData = MutableLiveData<DataState<FavouriteLocation>>()
    val deleteFavouriteLocation: LiveData<DataState<FavouriteLocation>>
        get() = _deleteFavouriteLocationLiveData

    val requestLocationPermissionSingleEvent = SingleLiveEvent<String>()

    private val _weatherBulletinLocationLiveData = MutableLiveData<DataState<WeatherBulletin>>()
    val weatherBulletinLocationLiveData: LiveData<DataState<WeatherBulletin>>
        get() = _weatherBulletinLocationLiveData

    private val _currentLocationLiveData = MutableLiveData<DataState<FavouriteLocation>>()
    val currentLocationLiveDataState: LiveData<DataState<FavouriteLocation>>
        get() = _currentLocationLiveData

    private val _favouritePlaceImageLiveData = MutableLiveData<DataState<LocationPicture>>()
    val favouritePlaceImageLiveData: LiveData<DataState<LocationPicture>>
        get() = _favouritePlaceImageLiveData


    fun setEvent(event: FavouritePlacesEvents) {
        viewModelScope.launch {
            when (event) {
                is FavouritePlacesEvents.LocationClicked -> {
                    repository.getWeatherBulletin(
                        WeatherRequestParams(
                            lat = event.favouriteLocation.lat,
                            lng = event.favouriteLocation.lng,
                            locationName = event.favouriteLocation.locationName,
                            locationId = event.favouriteLocation.locationId
                        )
                    ).onEach { dataState ->
                        _weatherBulletinLocationLiveData.value = dataState
                    }
                        .launchIn(viewModelScope)

                    repository.fetchPhoto(location = event.favouriteLocation)
                        .onEach {
                            _favouritePlaceImageLiveData.value = it
                        }
                        .launchIn(viewModelScope)
                }

                is FavouritePlacesEvents.SaveLocation -> {
                    val id = repository.saveFavouriteLocation(event.place)
                    if (id > -1) {
                        val location = repository.getLocation(event.place.id!!)
                        _favouriteLocationInsertedLiveData.value =
                            DataState.InsertLocation(location)
                    }
                }

                is FavouritePlacesEvents.GetFavouritePlacesLocations -> {
                    repository.getFavouriteLocations().onEach { dataState ->
                        _favouriteLocationsLiveData.value = dataState
                    }.launchIn(viewModelScope)
                }

                is FavouritePlacesEvents.DeleteLocation -> {
                    viewModelScope.launch(Dispatchers.IO) {
                        repository.deleteFavouriteLocation(favouriteLocation = event.favouriteLocation)

                        _deleteFavouriteLocationLiveData.value =
                            DataState.Generic(event.favouriteLocation)

                    }
                }

                is FavouritePlacesEvents.GetCurrentLocation -> {
                    repository.getCurrentLocationFromPlacesClient()
                        .onEach {
                            _currentLocationLiveData.value = it
                        }
                        .launchIn(viewModelScope)
                }

                is FavouritePlacesEvents.GetCurrentFusedLocation ->
                    repository.getCurrentLocationFromFusedLocationProvider()
                        .onEach {
                            _currentLocationLiveData.value = it
                        }
                        .launchIn(viewModelScope)
            }
        }
    }

}

