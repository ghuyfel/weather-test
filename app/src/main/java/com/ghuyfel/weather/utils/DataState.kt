package com.ghuyfel.weather.utils

sealed class DataState<out R> {
    data class Success<out T>(val data: T) : DataState<T>()
    data class Error(val exception: Exception): DataState<Nothing>()
    object Loading: DataState<Nothing>()
    data class InsertLocation<out T>(val data: T): DataState<T>()

    /**
     * We have to check the value of {@code T} to know if the request was successful.
     * This can be used when {@code T} has fields that are responses from different requests.
     * i.e. {@code WeatherBulletin}
     */
    data class Generic<out T>(val data:T): DataState<T>()

}
