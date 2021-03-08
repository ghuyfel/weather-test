package com.ghuyfel.weather.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.ghuyfel.weather.MainCoroutineScopeRule
import com.ghuyfel.weather.api.openweatherapi.models.params.WeatherRequestParams
import com.ghuyfel.weather.fakes.FakeObjects
import com.ghuyfel.weather.repository.Repository
import com.ghuyfel.weather.utils.DataState
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeFragmentViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineScopeRule()

    private lateinit var viewModel: HomeFragmentViewModel
    private lateinit var repository: Repository

    @Before
    fun setup() {
        repository = mockk()
        viewModel = HomeFragmentViewModel(SavedStateHandle(), repository)
    }

    @Test
    fun test_GetWeatherBulletinEvent_Loading() {
        val location = FakeObjects.getFavouriteLocation()
        val weatherRequestParams = WeatherRequestParams(
            lng = location.lng,
            lat = location.lat,
            locationName = location.locationName,
            locationId = location.locationId
        )

        val expected = DataState.Generic(FakeObjects.getWeatherBulletin())

        //given
        coEvery { repository.getWeatherBulletin(any()) } returns flow {
            emit(
                expected
            )
        }
        //when
        viewModel.setEvent(HomeFragmentEvents.GetWeatherBulletinEvent(weatherRequestParams))
        //then
        viewModel.weatherDataStateLiveData.observeForever{
            when(it){
                is DataState.Loading ->  assertThat(it).isEqualTo(DataState.Loading)
                else -> {}
            }
        }
    }

    @Test
    fun test_GetWeatherBulletinEvent_Generic() {
        val location = FakeObjects.getFavouriteLocation()
        val weatherRequestParams = WeatherRequestParams(
            lng = location.lng,
            lat = location.lat,
            locationName = location.locationName,
            locationId = location.locationId
        )

        val expected = DataState.Generic(FakeObjects.getWeatherBulletin())

        //given
        coEvery { repository.getWeatherBulletin(any()) } returns flow {
            emit(
                expected
            )
        }
        //when
        viewModel.setEvent(HomeFragmentEvents.GetWeatherBulletinEvent(weatherRequestParams))
        //then
        coVerify { repository.getWeatherBulletin(any()) }

        viewModel.weatherDataStateLiveData.observeForever {
            when (it) {
                is DataState.Generic -> {
                    assertThat(it.data).isEqualTo(expected.data)
                }
                else -> {}
            }
        }
    }
}
