package com.ghuyfel.weather.ui.interfaces

interface EventSetterInterface<in T> {
    fun setEvent(event: T)
}