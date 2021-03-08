package com.ghuyfel.weather.utils

import androidx.test.platform.app.InstrumentationRegistry
import com.ghuyfel.weather.R
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ResourceUtilsTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun test_getIconForWeatherCondition_rain() {
        val expected = R.drawable.clear
        val received = ResourceUtils.getIconForWeatherCondition("clear")
        assertThat(expected).isEqualTo(received)
    }

    @Test
    fun test_getIconForWeatherCondition_cloud() {
        val expected = R.drawable.cloudy
        val received = ResourceUtils.getIconForWeatherCondition("clouds")
        assertThat(expected).isEqualTo(received)
    }

    @Test
    fun test_getIconForWeatherCondition_clear() {
        val expected = R.drawable.rainy
        val received = ResourceUtils.getIconForWeatherCondition("rain")
        assertThat(expected).isEqualTo(received)
    }

    @Test
    fun getTextForWeatherCondition_rain() {
        val expected =  R.string.rainy
        val received = ResourceUtils.getTextForWeatherCondition( "rain")
        assertThat(expected).isEqualTo(received)
    }

    @Test
    fun getTextForWeatherCondition_cloud() {
        val expected = R.string.cloudy
        val received = ResourceUtils.getTextForWeatherCondition( "clouds")
        assertThat(expected).isEqualTo(received)
    }

    @Test
    fun getTextForWeatherCondition_clear() {
        val expected = R.string.sunny
        val received = ResourceUtils.getTextForWeatherCondition( "clear")
        assertThat(expected).isEqualTo(received)
    }

    @Test
    fun getBackgroundDrawableForWeatherCondition_rain() {
        val expected = R.drawable.forest_rainy
        val received = ResourceUtils.getBackgroundDrawableForWeatherCondition( "rain")
        assertThat(expected).isEqualTo(received)
    }

    @Test
    fun getBackgroundDrawableForWeatherCondition_clouds() {
        val expected = R.drawable.forest_cloudy
        val received = ResourceUtils.getBackgroundDrawableForWeatherCondition( "clouds")
        assertThat(expected).isEqualTo(received)
    }

    @Test
    fun getBackgroundDrawableForWeatherCondition_clear() {
        val expected = R.drawable.forest_sunny
        val received = ResourceUtils.getBackgroundDrawableForWeatherCondition( "clear")
        assertThat(expected).isEqualTo(received)
    }

    @Test
    fun getColourForWeatherCondition_clear() {
        val expected = R.color.sunny
        val received = ResourceUtils.getColourForWeatherCondition( "clear")
        assertThat(expected).isEqualTo(received)
    }

    @Test
    fun getColourForWeatherCondition_rain() {
        val expected = R.color.rainy
        val received = ResourceUtils.getColourForWeatherCondition( "rain")
        assertThat(expected).isEqualTo(received)
    }

    @Test
    fun getColourForWeatherCondition_clouds() {
        val expected = R.color.cloudy
        val received = ResourceUtils.getColourForWeatherCondition( "clouds")
        assertThat(expected).isEqualTo(received)
    }
}