package com.ghuyfel.weather.repository.mappers

import com.ghuyfel.weather.db.entities.LocationEntity
import com.ghuyfel.weather.repository.models.FavouriteLocation
import com.ghuyfel.weather.utils.MapperUtils
import javax.inject.Inject

class LocationMapper @Inject constructor() : MapperUtils<LocationEntity, FavouriteLocation> {

    override fun mapFromEntity(entity: LocationEntity): FavouriteLocation =
        FavouriteLocation(entity.locationId, entity.name, entity.lat, entity.lng, entity.address)

    override fun mapToEntity(uiModel: FavouriteLocation): LocationEntity =
        LocationEntity(
            uiModel.locationId,
            uiModel.locationName,
            uiModel.lat,
            uiModel.lng,
            uiModel.address
        )

    fun mapFromEntityList(entities: List<LocationEntity>): List<FavouriteLocation> =
        entities.map { mapFromEntity(it) }

    fun mapToEntityList(uiModels: List<FavouriteLocation>): List<LocationEntity> =
        uiModels.map { mapToEntity(it) }

}