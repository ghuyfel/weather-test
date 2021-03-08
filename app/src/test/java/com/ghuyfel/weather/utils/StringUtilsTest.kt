package com.ghuyfel.weather.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StringUtilsTest {

    @Test
    fun formatTemperature_roundDown() {
        val temp = 2.2
        val expected = "2°"
        val received = StringUtils.formatTemperature(temp)
        assertThat(received).isEqualTo(expected)
    }
    @Test
    fun formatTemperature_roundUp() {
        val temp = 2.5
        val expected = "3°"
        val received = StringUtils.formatTemperature(temp)
        assertThat(received).isEqualTo(expected)
    }
}