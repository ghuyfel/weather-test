package com.ghuyfel.weather.utils

interface MapperUtils <Entity, UIModel>{

    fun mapFromEntity(entity: Entity): UIModel

    fun mapToEntity(uiModel: UIModel): Entity

}