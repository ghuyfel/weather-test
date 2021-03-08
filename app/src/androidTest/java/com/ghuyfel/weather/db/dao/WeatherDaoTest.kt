package com.ghuyfel.weather.db.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.ghuyfel.weather.db.Database
import com.ghuyfel.weather.fakes.FakeObjects
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@HiltAndroidTest
@SmallTest
class WeatherDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var db: Database

    private lateinit var weatherDao: WeatherDao

    @Before
    fun setup() {
        hiltRule.inject()
        weatherDao = db.getWeatherDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun test_insert_forecast() = runBlockingTest {
        val forecast = FakeObjects.getForecastWeatherEntity()
        val id = weatherDao.insert(forecast)
        assertThat(id).isGreaterThan(-1)
    }

    @Test
    fun test_insert_current_weather() = runBlockingTest{
        val currentWeather = FakeObjects.getCurrentWeatherEntity()
        val id = weatherDao.insert(currentWeather)
        assertThat(id).isGreaterThan(-1)
    }

    @Test
    fun test_deleteAllForecastsForLocationId() = runBlockingTest{
        val list = FakeObjects.getForecastWeatherEntityList()
        val locationId = list[0].locationId

        for(forecast in list)
            weatherDao.insert(forecast)
        for(forecast in list)
            weatherDao.insert(forecast)

        weatherDao.deleteAllForecastsForLocationId(locationId)
        val forecastList = weatherDao.getForecastsForLocation(locationId)

        assertThat(forecastList).isEmpty()
    }

    @Test
    fun test_getForecastsForLocation() = runBlockingTest {
        val list = FakeObjects.getForecastWeatherEntityList()
        val locationName = list[0].locationName

        for(forecast in list)
            weatherDao.insert(forecast)
        for(forecast in list)
            weatherDao.insert(forecast)

        val forecastList = weatherDao.getForecastsForLocation(locationName)

        assertThat(forecastList.size).isEqualTo(2)

    }

    @Test
    fun test_getCurrentWeather_notNull() = runBlockingTest {
        val currentWeather = FakeObjects.getCurrentWeatherEntity()
        val locationName = currentWeather.locationName
        weatherDao.insert(currentWeather)
        val received = weatherDao.getCurrentWeather(locationName)

        assertThat(received).isNotNull()
    }

    @Test
    fun test_getCurrentWeather_correctLocationId() = runBlockingTest {
        val currentWeather = FakeObjects.getCurrentWeatherEntity()
        weatherDao.insert(currentWeather)
        val received = weatherDao.getCurrentWeather(currentWeather.locationName)
        assertThat(received?.locationId).isEqualTo(currentWeather.locationId)
    }

}