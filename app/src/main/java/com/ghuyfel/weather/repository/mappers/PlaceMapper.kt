package com.ghuyfel.weather.repository.mappers

import android.net.Uri
import android.os.Parcel
import com.ghuyfel.weather.db.entities.LocationEntity
import com.ghuyfel.weather.utils.MapperUtils
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.*
import javax.inject.Inject

class PlaceMapper @Inject constructor(): MapperUtils<LocationEntity, Place> {

    override fun mapFromEntity(entity: LocationEntity): Place {
        return object: Place() {
            override fun describeContents(): Int {
                return 0
            }

            override fun writeToParcel(dest: Parcel?, flags: Int) {}

            override fun getAddress(): String? = entity.address

            override fun getAddressComponents(): AddressComponents? = null

            override fun getBusinessStatus(): BusinessStatus? = null

            override fun getAttributions(): MutableList<String> = mutableListOf()

            override fun getId(): String = entity.locationId

            override fun getLatLng(): LatLng = LatLng(entity.lat, entity.lng)

            override fun getName(): String = entity.name

            override fun getOpeningHours(): OpeningHours? = null

            override fun getPhoneNumber(): String? = null

            override fun getPhotoMetadatas(): MutableList<PhotoMetadata> = mutableListOf()

            override fun getPlusCode(): PlusCode? = null

            override fun getPriceLevel(): Int? = null

            override fun getRating(): Double? = null

            override fun getTypes(): MutableList<Type> = mutableListOf()

            override fun getUserRatingsTotal(): Int? = null

            override fun getUtcOffsetMinutes(): Int? = null

            override fun getViewport(): LatLngBounds? {
                return LatLngBounds.Builder().include(LatLng(entity.lat, entity.lng)).build()
            }
            override fun getWebsiteUri(): Uri? = null
        }
    }

    override fun mapToEntity(uiModel: Place): LocationEntity {
        return LocationEntity(uiModel.id!!, uiModel.name!!, uiModel.latLng?.latitude!!, uiModel.latLng?.longitude!!, uiModel.address)
    }
}