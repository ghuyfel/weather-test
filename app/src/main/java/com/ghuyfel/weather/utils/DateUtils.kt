package com.ghuyfel.weather.utils

import android.content.Context
import com.ghuyfel.weather.R
import java.text.DateFormat
import java.time.format.DateTimeFormatter
import java.util.*

object DateUtils {
    fun getDayOfTheWeekFromMilliseconds(millis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        val dayOfTheWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return getDayOfTheWeekFromInteger(dayOfTheWeek)
    }

    private fun getDayOfTheWeekFromInteger(dayInt: Int): String {
        return when(dayInt) {
            1 -> "Sunday"
            2 -> "Monday"
            3 -> "Tuesday"
            4 -> "Wednesday"
            5 -> "Thursday"
            6 -> "Friday"
            7 -> "Saturday"
            else -> ""
        }
    }

    fun getElapsedTimeAsString(context: Context, millis: Long): String {
        val now = System.currentTimeMillis()
        val elapsed = now - (millis * 1000)

        return when {
            elapsed <= Constants.NOW -> {
                context.getString(R.string.now)
            }
            elapsed < Constants.ONE_HOUR -> {
                context.getString(R.string.less_than_an_hour)
            }
            elapsed >= Constants.ONE_HOUR && elapsed < Constants.TWO_HOURS-> {
                context.getString(R.string.an_hour_ago)
            }
            else -> {
                val calendar = Calendar.getInstance().apply { timeInMillis = (millis*1000) }
                val dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT)
               return dateFormat.format(calendar.time).capitalize(Locale.getDefault())
            }
        }

    }
}