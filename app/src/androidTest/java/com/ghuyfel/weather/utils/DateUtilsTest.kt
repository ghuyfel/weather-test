package com.ghuyfel.weather.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.ghuyfel.weather.R
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.DateFormat
import java.util.*

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@SmallTest
class DateUtilsTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val CURRENT_MILLIS = 1614567784L

    @Test
    fun test_getDayOfTheWeekFromMilliseconds() {
        val dayOfWeek = DateUtils.getDayOfTheWeekFromMilliseconds(CURRENT_MILLIS)
        assertThat(dayOfWeek).isEqualTo("Monday")
    }

    @Test
    fun test_getElapsedTimeAsString_Now() {
        val now = System.currentTimeMillis()/1000
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val received = DateUtils.getElapsedTimeAsString(context, now)
        val expected = context.getString(R.string.now)
        assertThat(received).isEqualTo(expected)
    }

    @Test
    fun test_getElapsedTimeAsString_AnHourAgo() {
        val now = System.currentTimeMillis()/1000 - (60*60)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val received = DateUtils.getElapsedTimeAsString(context, now)
        val expected = context.getString(R.string.an_hour_ago)
        assertThat(received).isEqualTo(expected)
    }

    @Test
    fun test_getElapsedTimeAsString_LessThanAnHour() {
        val now = System.currentTimeMillis()/1000 - (60*60 - 20)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val received = DateUtils.getElapsedTimeAsString(context, now)
        val expected = context.getString(R.string.less_than_an_hour)
        assertThat(received).isEqualTo(expected)
    }

    @Test
    fun test_getElapsedTimeAsString_Date() {
        val now = 1614567784L
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val received = DateUtils.getElapsedTimeAsString(context, now)
        val expected = getExpectedDateFor(now)
        assertThat(received).isEqualTo(expected)
    }

    private fun getExpectedDateFor(millis: Long): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = (millis*1000) }
        val dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT)
        return dateFormat.format(calendar.time).capitalize()
    }
}