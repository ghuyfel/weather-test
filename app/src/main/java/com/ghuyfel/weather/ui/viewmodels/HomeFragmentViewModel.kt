package com.ghuyfel.weather.ui.viewmodels

import androidx.lifecycle.*
import com.ghuyfel.weather.api.openweatherapi.models.params.WeatherRequestParams
import com.ghuyfel.weather.repository.Repository
import com.ghuyfel.weather.repository.models.WeatherBulletin
import com.ghuyfel.weather.utils.Constants
import com.ghuyfel.weather.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    val handle: SavedStateHandle,
    private val repository: Repository
) : ViewModel() {

    var lastUpdateTimeInMillis: Long = 0

    private val _weatherDataStateLiveData = MutableLiveData<DataState<WeatherBulletin>>()
    val weatherDataStateLiveData: LiveData<DataState<WeatherBulletin>> = _weatherDataStateLiveData

    fun setEvent(event: HomeFragmentEvents) {
        viewModelScope.launch {
            when (event) {
                is HomeFragmentEvents.GetWeatherBulletinEvent -> {
                    _weatherDataStateLiveData.postValue(DataState.Loading)

                    repository.getWeatherBulletin(event.params)
                        .onEach { dataState ->
                            _weatherDataStateLiveData.value = dataState

                        }.launchIn(viewModelScope)
                }

                else -> {
                }
            }
        }
    }

    fun shouldRefreshWeather(): Boolean =
        (lastUpdateTimeInMillis == 0L ||
                (System.currentTimeMillis() - lastUpdateTimeInMillis) >= Constants.REFRESH_INTERVAL)

}

sealed class HomeFragmentEvents {
    data class GetWeatherBulletinEvent(val params: WeatherRequestParams) : HomeFragmentEvents()
    object Loading : HomeFragmentEvents()
}