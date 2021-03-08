package com.ghuyfel.weather.repository

import com.ghuyfel.weather.api.openweatherapi.OpenWeatherApiService
import com.ghuyfel.weather.api.openweatherapi.models.params.WeatherRequestParams
import com.ghuyfel.weather.db.dao.LocationDao
import com.ghuyfel.weather.db.dao.WeatherDao
import com.ghuyfel.weather.repository.mappers.CurrentWeatherMapper
import com.ghuyfel.weather.repository.mappers.LocationMapper
import com.ghuyfel.weather.repository.mappers.PlaceMapper
import com.ghuyfel.weather.repository.mappers.WeatherForecastMapper
import com.ghuyfel.weather.repository.models.FavouriteLocation
import com.ghuyfel.weather.repository.models.WeatherBulletin
import com.ghuyfel.weather.ui.models.CurrentWeather
import com.ghuyfel.weather.ui.models.ForecastWeather
import com.ghuyfel.weather.ui.models.LocationPicture
import com.ghuyfel.weather.utils.Constants
import com.ghuyfel.weather.utils.DataState
import com.ghuyfel.weather.utils.DateUtils
import com.ghuyfel.weather.utils.PlaceNotFoundException
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class Repository(
    private val openWeatherApiService: OpenWeatherApiService,
    private val locationDao: LocationDao,
    private val locationMapper: LocationMapper,
    private val placeMapper: PlaceMapper,
    private val placesClient: PlacesClient,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val weatherDao: WeatherDao,
    private val weatherMapper: WeatherForecastMapper,
    private val currentWeatherMapper: CurrentWeatherMapper
) {

    suspend fun getFavouriteLocations(): Flow<DataState<List<FavouriteLocation>>> =
        flow {
            emit(DataState.Loading)
            val locationEntities = locationDao.getAllLocations()
            val favouriteLocations = locationMapper.mapFromEntityList(locationEntities)
            emit(DataState.Success(favouriteLocations))
        }

    suspend fun saveFavouriteLocation(place: Place): Long {
        val locationEntity = placeMapper.mapToEntity(place)
        return locationDao.insert(locationEntity)
    }

    suspend fun deleteFavouriteLocation(favouriteLocation: FavouriteLocation) {
        val locationEntity = locationMapper.mapToEntity(favouriteLocation)
        locationDao.delete(locationEntity)
    }

    private suspend fun getCurrentWeather(params: WeatherRequestParams): DataState<CurrentWeather> {
        try {
            val response = openWeatherApiService.getCurrentWeather(
                lat = params.lat,
                lon = params.lng,
                appid = Constants.API_KEY_OPENWEATHER_API,
                units = "metric"
            )

            val responseObject = response.body()

            if (response.isSuccessful && responseObject != null) {

                //we can use the mapper class here
                val currentWeather = CurrentWeather(
                    currentTemp = responseObject.main.temp,
                    maxTemp = responseObject.main.temp_max,
                    minTemp = responseObject.main.temp_min,
                    weather = responseObject.weather[0].main,
                    time = responseObject.dt.toLong(),
                    locationName = params.locationName,
                    locationId =  params.locationId
                )

                insertIntoCache(currentWeather)

                return DataState.Success(currentWeather)

            } else {
                return getCurrentWeatherFromCache(params.locationName)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return getCurrentWeatherFromCache(params.locationName)
        }
    }

    private suspend fun insertIntoCache(currentWeather: CurrentWeather) {
        val currentWeatherEntity = currentWeatherMapper.mapToEntity(currentWeather)
        weatherDao.insert(currentWeatherEntity)
    }

    private suspend fun getCurrentWeatherFromCache(
        locationName: String
    ): DataState<CurrentWeather> {
        val currentWeatherEntity = weatherDao.getCurrentWeather(
            locationName = locationName
        )
        return if (currentWeatherEntity == null) {
            DataState.Error(Exception("No data found."))
        } else {
            val currentWeather = currentWeatherMapper.mapFromEntity(currentWeatherEntity)
            DataState.Success(currentWeather)
        }

    }

    private suspend fun getForecastWeather(params: WeatherRequestParams): DataState<List<ForecastWeather>> {
        try {
            val response = openWeatherApiService.getForecastWeather(
                lat = params.lat,
                lon = params.lng,
                appid = Constants.API_KEY_OPENWEATHER_API,
                units = "metric"
            )

            val responseBody = response.body()
            if (response.isSuccessful && responseBody != null) {

                weatherDao.deleteAllForecastsForLocationId(params.locationName) // clear the table

                val daysOfTheWeek = ArrayList<String>()
                val uniqueDays = ArrayList<ForecastWeather>()

                for (forecast in responseBody.list) {
                    val day = DateUtils.getDayOfTheWeekFromMilliseconds(forecast.dt * 1000)
                    if (!daysOfTheWeek.contains(day)) {
                        val forecastWeather = ForecastWeather(
                            dayOfTheWeek = day,
                            weather = forecast.weather[0].main,
                            temp = forecast.main.temp,
                            time = forecast.dt,
                            locationName = params.locationName,
                            locationId = params.locationId
                        )
                        daysOfTheWeek.add(day)
                        uniqueDays.add(forecastWeather)

                        // cache data
                        insertIntoCache(forecastWeather)
                    }
                }
                return DataState.Success(uniqueDays)
            } else {
                return getForecastFromCache(params.locationName)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return getForecastFromCache(params.locationName)
        }
    }

    private suspend fun insertIntoCache(forecastWeather: ForecastWeather) {
        val forecastEntity = weatherMapper.mapToEntity(forecastWeather)
        weatherDao.insert(forecastEntity)
    }

    private suspend fun getForecastFromCache(
        locationName: String
    ): DataState<List<ForecastWeather>> {
        val listForecastEntities = weatherDao.getForecastsForLocation(
            locationName = locationName
        )
        return if (listForecastEntities.isEmpty())
            DataState.Error(Exception("No data found"))
        else {
            val forecastList = weatherMapper.mapFormEntityList(listForecastEntities)
            DataState.Success(forecastList)
        }
    }

    suspend fun getWeatherBulletin(params: WeatherRequestParams): Flow<DataState<WeatherBulletin>> =
        flow {
            emit(DataState.Loading)
            val currentWeather = getCurrentWeather(params)
            val forecastWeather = getForecastWeather(params)
            emit(
                DataState.Generic(
                    WeatherBulletin(
                        currentWeather = currentWeather,
                        forecastWeather = forecastWeather
                    )
                )
            )
        }

    suspend fun getLocation(id: String): FavouriteLocation {
        val locationEntity = locationDao.getLocation(id)
        return locationMapper.mapFromEntity(locationEntity)
    }

    @ExperimentalCoroutinesApi
    fun fetchPhoto(location: FavouriteLocation): Flow<DataState<LocationPicture>> =
        callbackFlow {
            val placeId = location.locationId

            val placeFields = listOf(
                Place.Field.PHOTO_METADATAS
            )

            val request = FetchPlaceRequest.newInstance(placeId, placeFields)

            placesClient.fetchPlace(request)
                .addOnSuccessListener { response: FetchPlaceResponse ->
                    val place = response.place
                    val metada = place.photoMetadatas
                    if (metada == null || metada.isEmpty()) {
                        return@addOnSuccessListener
                    }
                    val photoMetadata = metada.first()

                    val attributions = photoMetadata?.attributions

                    val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(Constants.MAX_PICTURE_WIDTH) // Optional.
                        .setMaxHeight(Constants.MAX_PICTURE_HEIGHT) // Optional.
                        .build()

                    placesClient.fetchPhoto(photoRequest)
                        .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                            val bitmap = fetchPhotoResponse.bitmap

                            offer(
                                DataState.Success(
                                    LocationPicture(
                                        attribution = attributions,
                                        bitmap = bitmap
                                    )
                                )
                            )

                        }.addOnFailureListener { exception: Exception ->
                            offer(DataState.Error(exception))
                        }

                }
                .addOnFailureListener { e: Exception ->
                    offer(DataState.Error(e))
                }

            awaitClose { }

        }

    @ExperimentalCoroutinesApi
    fun getCurrentLocationFromFusedLocationProvider(): Flow<DataState<FavouriteLocation>> =
        callbackFlow {
            try {
                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                    val currentLocation = FavouriteLocation(
                        locationId = "current",
                        locationName = "Current Location",
                        lat = location.latitude,
                        lng = location.longitude,
                        isCurrentLocation = true,
                        address = null
                    )
                    offer(DataState.Success(currentLocation))
                }
            } catch (e: SecurityException) {
                offer(DataState.Error(e))
            } catch (e: Exception) {
                offer(DataState.Error(e))
            }

            awaitClose { }
        }

    @ExperimentalCoroutinesApi
    fun getCurrentLocationFromPlacesClient(): Flow<DataState<FavouriteLocation>> =
        callbackFlow {
            val placeFields: List<Place.Field> =
                listOf(Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.NAME, Place.Field.ID)

            val request: FindCurrentPlaceRequest =
                FindCurrentPlaceRequest.newInstance(placeFields)

            try {
                val placeResponse = placesClient.findCurrentPlace(request)
                placeResponse.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val response = task.result

                        response?.let {
                            val list = ArrayList<PlaceLikelihood>()
                            list.addAll(it.placeLikelihoods)
                            if (list.isNotEmpty()) {
                                list.sortByDescending { e -> e.likelihood }
                                val place = it.placeLikelihoods[0].place
                                val currentLocation = FavouriteLocation(
                                    place.id!!,
                                    place.name!!,
                                    place.latLng?.latitude!!,
                                    place.latLng?.longitude!!,
                                    place.address
                                )
                                offer(DataState.Success(currentLocation))

                            } else {
                                offer(DataState.Error(PlaceNotFoundException()))
                            }
                        }
                    } else {
                        val exception = task.exception
                        if (exception is ApiException) {
                            offer(DataState.Error(PlaceNotFoundException()))
                        } else {
                            offer(DataState.Error(Exception(exception)))
                        }
                    }
                }
            } catch (e: SecurityException) {
                offer(DataState.Error(e))
            } catch (e: Exception) {
                e.printStackTrace()
                offer(DataState.Error(PlaceNotFoundException()))
            }

            awaitClose { }
        }

}