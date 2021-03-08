package com.ghuyfel.weather.repository.mappers

import com.ghuyfel.weather.db.entities.CurrentWeatherEntity
import com.ghuyfel.weather.ui.models.CurrentWeather
import com.ghuyfel.weather.utils.MapperUtils
import javax.inject.Inject
import kotlin.concurrent.timerTask

class CurrentWeatherMapper @Inject constructor() :
    MapperUtils<CurrentWeatherEntity, CurrentWeather> {
    override fun mapFromEntity(entity: CurrentWeatherEntity): CurrentWeather =
        CurrentWeather(
            currentTemp = entity.currentTemp,
            minTemp = entity.minTemp,
            maxTemp = entity.maxTemp,
            weather = entity.weather,
            time = entity.time,
            locationName = entity.locationName,
            locationId = entity.locationId
        )

    override fun mapToEntity(uiModel: CurrentWeather): CurrentWeatherEntity =
        CurrentWeatherEntity(
            locationId = uiModel.locationId,
            locationName = uiModel.locationName,
            currentTemp = uiModel.currentTemp,
            minTemp = uiModel.minTemp,
            maxTemp = uiModel.maxTemp,
            weather = uiModel.weather,
            time = uiModel.time
        )
}