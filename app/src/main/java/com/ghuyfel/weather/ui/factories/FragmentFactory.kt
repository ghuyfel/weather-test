package com.ghuyfel.weather.ui.factories

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.ghuyfel.weather.permissions.LocationPermissionHelper
import com.ghuyfel.weather.ui.adapters.FavouriteLocationAdapter
import com.ghuyfel.weather.ui.adapters.FavouriteWeatherAdapter
import com.ghuyfel.weather.ui.adapters.ForecastAdapter
import com.ghuyfel.weather.ui.fragments.FavouritePlacesFragment
import com.ghuyfel.weather.ui.fragments.HomeFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class FragmentFactory @Inject constructor(
    private val forecastAdapter: ForecastAdapter,
    private val favouriteLocationAdapter: FavouriteLocationAdapter,
    private val favouriteWeatherAdapter: FavouriteWeatherAdapter,
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            HomeFragment::class.java.name -> HomeFragment(
                forecastAdapter
            )
            FavouritePlacesFragment::class.java.name -> FavouritePlacesFragment(
                favouriteLocationAdapter,
                favouriteWeatherAdapter
            )
            else -> super.instantiate(classLoader, className)
        }
    }
}