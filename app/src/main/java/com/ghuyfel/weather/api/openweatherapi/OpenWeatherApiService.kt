package com.ghuyfel.weather.api.openweatherapi

import com.ghuyfel.weather.api.openweatherapi.models.responses.GetCurrentWeatherResponse
import com.ghuyfel.weather.api.openweatherapi.models.responses.GetForecastWeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApiService {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Response<GetCurrentWeatherResponse>

    @GET("forecast")
    suspend fun getForecastWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Response<GetForecastWeatherResponse>
}