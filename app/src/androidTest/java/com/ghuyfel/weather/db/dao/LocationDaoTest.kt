package com.ghuyfel.weather.db.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.ghuyfel.weather.db.Database
import com.ghuyfel.weather.fakes.FakeObjects
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
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
class LocationDaoTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var db: Database

    private lateinit var locationDao: LocationDao

    @Before
    fun setup() {
        hiltRule.inject()
        locationDao = db.getLocationDao()
    }

    @After
    fun teardown() {
        db.close()
    }


    @Test
    fun test_insert() = runBlocking {
        val location = FakeObjects.getLocationEntity()
        val id = locationDao.insert(location)
        assertThat(id).isGreaterThan(-1)
    }

    @Test
    fun test_delete() = runBlocking {
        val location = FakeObjects.getLocationEntity()
        val locationId = location.locationId
        locationDao.insert(location)
        locationDao.delete(location)
        val received = locationDao.getLocation(locationId)

        assertThat(received).isNull()
    }

    @Test
    fun test_getAllLocations_notEmpty() = runBlockingTest{
        val list = FakeObjects.getLocationEntityList()
        for(location in list)
            locationDao.insert(location)
        val received = locationDao.getAllLocations()

        assertThat(received).isNotEmpty()
    }

    @Test
    fun test_getAllLocations_correctSize() = runBlockingTest{
        val list = FakeObjects.getLocationEntityList()
        for(location in list)
            locationDao.insert(location)
        val received = locationDao.getAllLocations()

        assertThat(received.size).isEqualTo(list.size)
    }

    @Test
    fun test_getLocation_notNull() = runBlockingTest {
        val location = FakeObjects.getLocationEntity()
        val locationId = location.locationId
        locationDao.insert(location)
        val received = locationDao.getLocation(locationId)
        assertThat(received).isNotNull()
    }
    @Test
    fun test_getLocation_correctObject() = runBlockingTest {
        val location = FakeObjects.getLocationEntity()
        val locationId = location.locationId
        locationDao.insert(location)
        val received = locationDao.getLocation(locationId)
        assertThat(received.locationId).isEqualTo(locationId)
    }
}