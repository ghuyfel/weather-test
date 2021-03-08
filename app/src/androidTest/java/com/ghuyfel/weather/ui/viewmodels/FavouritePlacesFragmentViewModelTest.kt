package com.ghuyfel.weather.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ghuyfel.weather.MainCoroutineScopeRule
import com.ghuyfel.weather.fakes.FakeObjects
import com.ghuyfel.weather.repository.Repository
import com.ghuyfel.weather.ui.events.FavouritePlacesEvents
import com.ghuyfel.weather.utils.DataState
import com.ghuyfel.weather.utils.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class FavouritePlacesFragmentViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineScopeRule()

    private lateinit var viewModel: FavouritePlacesFragmentViewModel
    private lateinit var repository: Repository

    @Before
    fun setup() {
        repository = mockk()
        viewModel = FavouritePlacesFragmentViewModel(repository)
    }

    @Test
    fun test_getCurrentLocation_event() = runBlockingTest{
        val expected = DataState.Success(FakeObjects.getFavouriteLocation())
        //given
        coEvery { repository.getCurrentLocationFromPlacesClient() } returns flow {
            emit(
                DataState.Success(
                    FakeObjects.getFavouriteLocation()
                )
            )
        }
        //when
        viewModel.setEvent(FavouritePlacesEvents.GetCurrentLocation)
        //then
        verify { repository.getCurrentLocationFromPlacesClient() }
        val dataState = viewModel.currentLocationLiveDataState.getOrAwaitValue(2, TimeUnit.SECONDS)
        assertThat(dataState).isEqualTo(expected)
    }

    @Test
    fun test_LocationClicked_event() = runBlockingTest {

        val location = FakeObjects.getFavouriteLocation()
        val expected = DataState.Success(FakeObjects.getWeatherBulletin())

        //given
        coEvery {
            repository.getWeatherBulletin(any())
        } returns flow {
            emit(expected)
        }

        every { repository.fetchPhoto(any()) } returns flow {
            emit(DataState.Success(FakeObjects.getNullLocationPicture()))
        }
        //when
        viewModel.setEvent(FavouritePlacesEvents.LocationClicked(location))
        //then
        coVerify {
            repository.getWeatherBulletin(any())
        }

        val dataState =
            viewModel.weatherBulletinLocationLiveData.getOrAwaitValue(2, TimeUnit.SECONDS)

        assertThat(dataState).isEqualTo(expected)
    }

    @Test
    fun test_GetFavouritePlacesLocations_event() = runBlockingTest{
        val expected = DataState.Success(FakeObjects.getFavouriteLocationsList())
        //given
        coEvery { repository.getFavouriteLocations() } returns flow {
            emit(
                DataState.Success(
                    FakeObjects.getFavouriteLocationsList()
                )
            )
        }
        //when
        viewModel.setEvent(FavouritePlacesEvents.GetFavouritePlacesLocations)
        //then
        coVerify { repository.getFavouriteLocations() }

        val dataState = viewModel.favouriteLocationsLiveData.getOrAwaitValue(2, TimeUnit.SECONDS)
        assertThat((dataState as DataState.Success).data.size).isEqualTo(expected.data.size)
    }

    @Test
    fun test_GetCurrentFusedLocation() = runBlockingTest{
        val expected = DataState.Success(FakeObjects.getFavouriteLocation())
        //given
        every { repository.getCurrentLocationFromFusedLocationProvider() } returns flow {
            emit(
                DataState.Success(
                    FakeObjects.getFavouriteLocation()
                )
            )
        }
        //when
        viewModel.setEvent(FavouritePlacesEvents.GetCurrentFusedLocation)
        //then
        verify { repository.getCurrentLocationFromFusedLocationProvider() }
        val dataState = viewModel.currentLocationLiveDataState.getOrAwaitValue(2, TimeUnit.SECONDS)
        assertThat(dataState).isEqualTo(expected)
    }

    @Test
    fun test_DeleteLocation_event() = runBlockingTest{
        val location = FakeObjects.getFavouriteLocation()
        //given
        coJustRun {
            repository.deleteFavouriteLocation(any())
        }

        //when
        viewModel.setEvent(FavouritePlacesEvents.DeleteLocation(location))


        val dataState =
            viewModel.deleteFavouriteLocation.getOrAwaitValue(2, TimeUnit.SECONDS)

        //then
        coVerify {
            repository.deleteFavouriteLocation(any())
        }

       when(dataState) {
           is DataState.Generic -> {
               assertThat(dataState.data.locationId).isEqualTo(location.locationId)
           }
           else -> assert(false)
       }

    }
}