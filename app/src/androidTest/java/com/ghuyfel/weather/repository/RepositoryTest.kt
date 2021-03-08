package com.ghuyfel.weather.repository

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnitRunner
import com.ghuyfel.weather.HiltTestRunner
import com.ghuyfel.weather.api.openweatherapi.OpenWeatherApiService
import com.ghuyfel.weather.api.openweatherapi.models.params.WeatherRequestParams
import com.ghuyfel.weather.db.Database
import com.ghuyfel.weather.db.dao.LocationDao
import com.ghuyfel.weather.db.dao.WeatherDao
import com.ghuyfel.weather.fakes.FakeObjects
import com.ghuyfel.weather.repository.mappers.CurrentWeatherMapper
import com.ghuyfel.weather.repository.mappers.LocationMapper
import com.ghuyfel.weather.repository.mappers.PlaceMapper
import com.ghuyfel.weather.repository.mappers.WeatherForecastMapper
import com.ghuyfel.weather.repository.models.WeatherBulletin
import com.ghuyfel.weather.utils.DataState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.common.graph.SuccessorsFunction
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@HiltAndroidTest
@SmallTest
class RepositoryTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instaTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var locationMapper: LocationMapper

    @Inject
    lateinit var currentWeatherMapper: CurrentWeatherMapper

    @Inject
    lateinit var weatherMapper: WeatherForecastMapper

    @Inject
    lateinit var placeMapper: PlaceMapper

    @Inject
    @Named("test_db")
    lateinit var db: Database

    lateinit var weatherDao: WeatherDao
    lateinit var locationDao: LocationDao

    lateinit var openWeatherApiService: OpenWeatherApiService
    lateinit var placesClient: PlacesClient
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    lateinit var repository: Repository

    @Before
    fun setup() {
        hiltRule.inject()
        weatherDao = db.getWeatherDao()
        locationDao = db.getLocationDao()

        openWeatherApiService = mockk()
        placesClient = mockk()
        fusedLocationProviderClient = mockk()

        repository = Repository(
            weatherDao = weatherDao,
            locationDao = locationDao,
            locationMapper = locationMapper,
            currentWeatherMapper = currentWeatherMapper,
            placeMapper = placeMapper,
            weatherMapper = weatherMapper,
            openWeatherApiService = openWeatherApiService,
            placesClient = placesClient,
            fusedLocationProviderClient = fusedLocationProviderClient
        )

        insertLocations()

        initWeatherCache()

    }

    private fun initWeatherCache() = runBlockingTest {
        val currentWeatherEntity = currentWeatherMapper.mapToEntity(FakeObjects.getCurrentWeather())
        weatherDao.insert(currentWeatherEntity)

        val forecasts = FakeObjects.getForecastWeather()
        for (forecast in forecasts) {
            val forecastEntity = weatherMapper.mapToEntity(forecast)
            weatherDao.insert(forecastEntity)
        }
    }

    private fun insertLocations() = runBlockingTest {
        val locations = locationMapper.mapToEntityList(FakeObjects.getFavouriteLocationsList())
        for (location in locations) {
            locationDao.insert(location)
        }
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun test_getFavouriteLocations() = runBlockingTest {
        val expected = FakeObjects.getFavouriteLocationsList()
        repository.getFavouriteLocations().collectLatest {
            when (it) {
                is DataState.Success -> {
                    assertThat(it.data).isEqualTo(expected)
                }
                else -> {
                    assertThat(it).isEqualTo(DataState.Loading)
                }
            }
        }
    }

    @Test
    fun test_saveFavouriteLocation() = runBlockingTest {
        val place = FakeObjects.getPlace()
        val locationId = place.id
        repository.saveFavouriteLocation(place)
        val location = repository.getLocation(locationId!!)
        assertThat(locationId).isEqualTo(location.locationId)
    }

    @Test
    fun test_deleteFavouriteLocation() = runBlockingTest {
        val location = FakeObjects.getFavouriteLocation()
        repository.deleteFavouriteLocation(location)
        val expected = locationDao.getLocation(location.locationId)
        assertThat(expected).isNull()
    }

    @Test
    fun test_getWeatherBulletin_fromNetwork() = runBlockingTest {
        //Given
        val timestamp = System.currentTimeMillis()
        val expected = WeatherBulletin(
            currentWeather = DataState.Success(FakeObjects.getCurrentWeather()),
            forecastWeather = DataState.Success(FakeObjects.getForecastWeather()),
            timestamp = timestamp
        )

        val location = FakeObjects.getFavouriteLocation()
        val weatherRequestParams = WeatherRequestParams(
            lng = location.lng,
            lat = location.lat,
            locationName = location.locationName,
            locationId = location.locationId
        )

        coEvery {
            openWeatherApiService.getCurrentWeather(
                any(),
                any(),
                any(),
                any()
            )
        } returns FakeObjects.getCurrentWeatherSuccessResponse()

        coEvery {
            openWeatherApiService.getForecastWeather(
                any(),
                any(),
                any(),
                any()
            )
        } returns FakeObjects.getForecastWeatherSuccessResponse()
        //when

        repository.getWeatherBulletin(weatherRequestParams).onEach { dataState ->

            when (dataState) {
                is DataState.Generic -> {
                    //then
                    coVerifyAll {
                        openWeatherApiService.getCurrentWeather(any(), any(), any(), any())
                        openWeatherApiService.getForecastWeather(any(), any(), any(), any())
                    }
                    val received = dataState.data
                    //timestamp has a default value. We need to do this in order to have the correct expected value
                    received.timestamp = timestamp
                    assertThat(received).isEqualTo(expected)
                }

                DataState.Loading -> {
                }
                else -> {
                    assert(false)
                }
            }
        }
    }

    @Test
    fun test_getWeatherBulletin_fromCache() = runBlockingTest {
        //Given
        val timestamp = System.currentTimeMillis()
        val expected = WeatherBulletin(
            currentWeather = DataState.Success(FakeObjects.getCurrentWeather()),
            forecastWeather = DataState.Success(FakeObjects.getForecastWeather()),
            timestamp = timestamp
        )

        val location = FakeObjects.getFavouriteLocation()
        val weatherRequestParams = WeatherRequestParams(
            lng = location.lng,
            lat = location.lat,
            locationName = location.locationName,
            locationId = location.locationId
        )

        coEvery {
            openWeatherApiService.getCurrentWeather(
                any(),
                any(),
                any(),
                any()
            )
        } returns FakeObjects.getCurrentWeatherFailureResponse()

        coEvery {
            openWeatherApiService.getForecastWeather(
                any(),
                any(),
                any(),
                any()
            )
        } returns FakeObjects.getForecastWeatherFailureResponse()
        //when

        repository.getWeatherBulletin(weatherRequestParams).onEach { dataState ->

            when (dataState) {
                is DataState.Generic -> {
                    //then
                    coVerifyAll {
                        openWeatherApiService.getCurrentWeather(any(), any(), any(), any())
                        openWeatherApiService.getForecastWeather(any(), any(), any(), any())
                    }
                    val received = dataState.data
                    //timestamp has a default value. We need to do this in order to have the correct expected value
                    received.timestamp = timestamp
                    assertThat(received).isEqualTo(expected)
                }

                DataState.Loading -> {
                }
                else -> {
                    assert(false)
                }
            }
        }
    }

    @Test
    fun test_fetchPhoto() = runBlockingTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val location = FakeObjects.getFavouriteLocation()

        //When mocking Tasks and listeners it's important to mock answers or the test will
        //never complete. For that we use slots that we can capture.

        val fetchPlaceResponse = mockk<FetchPlaceResponse>()
        every { fetchPlaceResponse.place } returns FakeObjects.getPlace()
        val fetchPlaceTask = mockk<Task<FetchPlaceResponse>>()
        every { fetchPlaceTask.isSuccessful } returns true
        every { fetchPlaceTask.result } returns fetchPlaceResponse
        val placeSlotOnSuccess = slot<OnSuccessListener<FetchPlaceResponse>>()
        every { fetchPlaceTask.addOnSuccessListener(capture(placeSlotOnSuccess)) } answers {
            placeSlotOnSuccess.captured.onSuccess(fetchPlaceResponse)
            fetchPlaceTask
        }
        val placeSlotOnFailure = slot<OnFailureListener>()
        every { fetchPlaceTask.addOnFailureListener(capture(placeSlotOnFailure)) } answers {
            placeSlotOnFailure.captured.onFailure(Exception("Fetch place failed."))
            fetchPlaceTask
        }

        every {
            placesClient.fetchPlace(any()).addOnSuccessListener(capture(placeSlotOnSuccess))
        } answers {
            placeSlotOnSuccess.captured.onSuccess(fetchPlaceResponse)
            fetchPlaceTask
        }

        every {
            placesClient.fetchPlace(any()).addOnFailureListener(capture(placeSlotOnFailure))
        } answers {
            placeSlotOnFailure.captured.onFailure(Exception("Fetch place failed."))
            fetchPlaceTask
        }


        val fetchPhotoResponse = mockk<FetchPhotoResponse>()
        every { fetchPhotoResponse.bitmap } returns FakeObjects.getLocationPicture(context).bitmap!!
        val fetchPhotoTask = mockk<Task<FetchPhotoResponse>>()
        every { fetchPhotoTask.isSuccessful } returns true
        every { fetchPhotoTask.result } returns fetchPhotoResponse
        val photoSlotOnSuccess = slot<OnSuccessListener<FetchPhotoResponse>>()
        every { fetchPhotoTask.addOnSuccessListener(capture(photoSlotOnSuccess)) } answers {
            photoSlotOnSuccess.captured.onSuccess(fetchPhotoResponse)
            fetchPhotoTask
        }
        val photoSlotOnFailure = slot<OnFailureListener>()
        every { fetchPhotoTask.addOnFailureListener(capture(photoSlotOnFailure)) } answers {
            photoSlotOnFailure.captured.onFailure(Exception("Fetch photo failed."))
            fetchPhotoTask
        }

        every {
            placesClient.fetchPhoto(any()).addOnSuccessListener(capture(photoSlotOnSuccess))
        } answers {
            photoSlotOnSuccess.captured.onSuccess(fetchPhotoResponse)
            fetchPhotoTask
        }

        every {
            placesClient.fetchPhoto(any()).addOnFailureListener(capture(photoSlotOnFailure))
        } answers {
            photoSlotOnFailure.captured.onFailure(Exception("Fetch photo failed."))
            fetchPhotoTask
        }

        //when
        repository.fetchPhoto(location).onEach { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    //then
                    verifyAll {
                        placesClient.fetchPlace(any())
                        placesClient.fetchPhoto(any())
                    }
                    assertThat(dataState.data.bitmap).isNotNull()
                }
                is DataState.Loading -> {
//                    assert(true)
                }
                else -> assert(false)
            }
        }
    }

    @Test
    fun test_getCurrentLocationFromFusedLocationProvider() = runBlockingTest {
        val currentLocation = mockk<Location>()
        every { currentLocation.latitude } returns 2.2
        every { currentLocation.longitude } returns 2.2

        val currentLocationTask = mockk<Task<Location>>()
        every { currentLocationTask.result } returns FakeObjects.getLocation()
        every { currentLocationTask.isSuccessful } returns true

        val currentLocationSlot = slot<OnSuccessListener<Location>>()
        every { currentLocationTask.addOnSuccessListener(capture(currentLocationSlot)) } answers {
            currentLocationSlot.captured.onSuccess(currentLocation)
            currentLocationTask
        }

        every {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener(
                capture(
                    currentLocationSlot
                )
            )
        } answers {
            currentLocationSlot.captured.onSuccess(currentLocation)
            currentLocationTask
        }

        repository.getCurrentLocationFromFusedLocationProvider().onEach { dataSet ->
            when (dataSet) {
                is DataState.Success -> {
                    val location = dataSet.data
                    // we check against the default values of lat and lng because they are not nullable
                    assertThat(location.lat).isNotEqualTo(0)
                    assertThat(location.lng).isNotEqualTo(0)
                }
                is DataState.Loading -> {
                }
                else -> assert(false)
            }
        }
    }

    @Test
    fun test_getCurrentLocationFromPlacesClient() {
        val currentLocation = mockk<FindCurrentPlaceResponse>()
        every { currentLocation.placeLikelihoods } returns FakeObjects.getPlaceLikelihoods()

        val currentLocationTask = mockk<Task<FindCurrentPlaceResponse>>()
        every { currentLocationTask.isSuccessful } returns true
        every { currentLocationTask.result } returns FakeObjects.getFindCurrentPlaceResponse()

        val currentPlaceSlot = slot<OnSuccessListener<FindCurrentPlaceResponse>>()
        every { currentLocationTask.addOnSuccessListener(capture(currentPlaceSlot)) } answers {
            currentPlaceSlot.captured.onSuccess(currentLocation)
            currentLocationTask
        }

        every {
            placesClient.findCurrentPlace(any()).addOnSuccessListener(capture(currentPlaceSlot))
        } answers {
            currentPlaceSlot.captured.onSuccess(currentLocation)
            currentLocationTask
        }

        repository.getCurrentLocationFromPlacesClient().onEach { dataState ->
            when(dataState) {
                is DataState.Success -> {
                    verify {
                        placesClient.findCurrentPlace(any())
                    }
                    val location = dataState.data
                    assertThat(location).isNotNull()
                }
                is DataState.Loading -> { }
                else -> assert(false)
            }
        }
    }

    @Test
    fun getLocationTest() = runBlockingTest{
        val location = repository.getLocation("someLocationId2")
        assertThat(location).isNotNull()
    }
}

