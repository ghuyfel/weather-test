package com.ghuyfel.weather

import com.ghuyfel.weather.db.dao.LocationDaoTest
import com.ghuyfel.weather.db.dao.WeatherDaoTest
import com.ghuyfel.weather.repository.RepositoryTest
import com.ghuyfel.weather.ui.viewmodels.FavouritePlacesFragmentViewModelTest
import com.ghuyfel.weather.ui.viewmodels.HomeFragmentViewModelTest
import com.ghuyfel.weather.utils.DateUtilsTest
import com.ghuyfel.weather.utils.ResourceUtilsTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.runners.Suite

@ExperimentalCoroutinesApi
@RunWith(Suite::class)
@Suite.SuiteClasses(
    ResourceUtilsTest::class,
    DateUtilsTest::class,
    FavouritePlacesFragmentViewModelTest::class,
    LocationDaoTest::class,
    WeatherDaoTest::class,
    HomeFragmentViewModelTest::class,
    RepositoryTest::class
)
class InstrumentationTestSuite