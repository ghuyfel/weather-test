package com.ghuyfel.weather.repository.mappers

import com.ghuyfel.weather.db.entities.ForecastWeatherEntity
import com.ghuyfel.weather.ui.models.ForecastWeather
import com.ghuyfel.weather.utils.MapperUtils
import javax.inject.Inject

class WeatherForecastMapper @Inject constructor() :
    MapperUtils<ForecastWeatherEntity, ForecastWeather> {
    override fun mapFromEntity(entity: ForecastWeatherEntity): ForecastWeather {
        return ForecastWeather(
            dayOfTheWeek = entity.dayOfTheWeek,
            weather = entity.weather,
            temp = entity.temp,
            time = entity.time,
            locationId = entity.locationId,
            locationName = entity.locationName
        )
    }

    override fun mapToEntity(uiModel: ForecastWeather): ForecastWeatherEntity {
        return ForecastWeatherEntity(
            locationId = uiModel.locationId,
            locationName = uiModel.locationName,
            dayOfTheWeek = uiModel.dayOfTheWeek,
            weather = uiModel.weather,
            temp = uiModel.temp,
            time = uiModel.time
        )
    }

    fun mapFormEntityList(entities: List<ForecastWeatherEntity>): List<ForecastWeather> =
        entities.map { mapFromEntity(it) }

    fun mapToEntityList(uiModels: List<ForecastWeather>): List<ForecastWeatherEntity> =
        uiModels.map { mapToEntity(it) }
}