package com.ghuyfel.weather.ui.fragments

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ghuyfel.weather.R
import com.ghuyfel.weather.api.openweatherapi.models.params.WeatherRequestParams
import com.ghuyfel.weather.databinding.FragmentHomeBinding
import com.ghuyfel.weather.permissions.LocationPermissionHelper
import com.ghuyfel.weather.ui.adapters.ForecastAdapter
import com.ghuyfel.weather.ui.models.CurrentWeather
import com.ghuyfel.weather.ui.models.ForecastWeather
import com.ghuyfel.weather.ui.viewmodels.HomeFragmentEvents
import com.ghuyfel.weather.ui.viewmodels.HomeFragmentViewModel
import com.ghuyfel.weather.utils.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class HomeFragment(
    private val adapter: ForecastAdapter
) : Fragment(), ActivityResultCallback<Map<String, Boolean>> {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel by viewModels<HomeFragmentViewModel>()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var currentLocation: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions(), this
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        subscribeObservers()

        binding = FragmentHomeBinding.inflate(layoutInflater)

        binding.rvWeekForecast.setHasFixedSize(true)
        binding.rvWeekForecast.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWeekForecast.adapter = adapter
        binding.rvWeekForecast.overScrollMode = OVER_SCROLL_NEVER

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.swipeRefresh.setOnRefreshListener {
            getLastLocation(true)
        }

        return binding.root
    }

    private fun handleLocationPermissions() {
        when {
            LocationPermissionHelper.isPermissionGranted(requireContext()) -> getLastLocation(false)
            LocationPermissionHelper.shouldShowRationale(requireActivity()) -> showLocationPermissionRationale()
            else -> LocationPermissionHelper.requestLocationPermissions(locationPermissionLauncher)
        }
    }

    private fun showLocationPermissionRationale() {
        MaterialAlertDialogBuilder(requireContext())
            .setCancelable(false)
            .setTitle(R.string.location_permission)
            .setMessage(R.string.location_permission_needed)
            .setPositiveButton(R.string.request_permission) { dialog, _ ->
                run {
                    LocationPermissionHelper.requestLocationPermissions(locationPermissionLauncher)
                    dialog.cancel()
                }
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                run {
                    showCurrentWeatherErrorMessage(Exception(getString(R.string.location_permission_denied)))
                    dialog.cancel()
                }
            }
            .show()
    }

    private fun getLastLocation(force: Boolean) {

        try {
            if (viewModel.shouldRefreshWeather() || force) {
                
                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->

                    binding.swipeRefresh.isRefreshing = false

                    if (location == null) {
                        showCurrentWeatherErrorMessage(
                            Exception(
                                getString(
                                    R.string.something_went_wrong,
                                    getString(R.string.failed_to_get_location)
                                )
                            )
                        )
                        return@addOnSuccessListener
                    }

                    viewModel.setEvent(
                        HomeFragmentEvents.GetWeatherBulletinEvent(
                            WeatherRequestParams(
                                lng = location.longitude,
                                lat = location.latitude,
                                locationName = getString(R.string.current)
                            )
                        )
                    )

                    currentLocation = location

                }
            }
        } catch (e: SecurityException) {
            LocationPermissionHelper.requestLocationPermissions(locationPermissionLauncher)
        }

    }

    private fun showPermissionDenied() {
        hideLoadingView()
        showCurrentWeatherErrorMessage(
            Exception(
                getString(
                    R.string.location_permission_denied
                )
            )
        )
    }

    private fun subscribeObservers() {
        viewModel.weatherDataStateLiveData.observe(viewLifecycleOwner) { weather ->
            binding.swipeRefresh.isRefreshing = false

            when (weather) {
                is DataState.Generic -> {
                    viewModel.lastUpdateTimeInMillis = weather.data.timestamp
                    handleCurrentWeatherState(weather.data.currentWeather)
                    handleForecastState(weather.data.forecastWeather)
                    hideLoadingView()
                }

                is DataState.Loading -> {
                    showLoadingView()
                }

                else -> {
                    showGenericErrorDialog()
                }
            }

        }
    }

    private fun showGenericErrorDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.error)
            .setMessage(getString(R.string.something_went_wrong, ""))
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun hideLoadingView() {
        ViewAnimationUtils.fadeOut(binding.llLoading)
    }

    private fun showLoadingView() {
        binding.llLoading.visibility = VISIBLE
        binding.llCurrentError.visibility = GONE
        binding.llCurrentWeather.visibility = GONE
        binding.llForecastError.visibility = GONE
        binding.rvWeekForecast.visibility = GONE
    }

    private fun handleForecastState(forecastWeather: DataState<List<ForecastWeather>>) {
        when (forecastWeather) {
            is DataState.Success -> {
                showForecastWeatherView(forecastWeather.data)
            }

            is DataState.Error -> {
                showForecastErrorView(forecastWeather.exception)
            }

            else -> showGenericErrorDialog()
        }
    }

    private fun showForecastErrorView(exception: Exception) {
        binding.llForecastError.visibility = VISIBLE
        binding.rvWeekForecast.visibility = GONE
        binding.tvForecastError.text = getString(R.string.something_went_wrong, exception.message)
    }

    private fun showForecastWeatherView(forecastWeather: List<ForecastWeather>) {
        binding.llForecastError.visibility = GONE
        binding.rvWeekForecast.visibility = INVISIBLE
        adapter.submitList(forecastWeather)
        ViewAnimationUtils.fadeIn(binding.rvWeekForecast)
    }

    private fun handleCurrentWeatherState(currentWeatherDataState: DataState<CurrentWeather>) {
        binding.swipeRefresh.isRefreshing = false

        when (currentWeatherDataState) {
            is DataState.Success -> {
                val currentWeather: CurrentWeather = currentWeatherDataState.data
                showCurrentWeatherViews(currentWeather)
            }

            is DataState.Error -> {
                showCurrentWeatherErrorMessage(currentWeatherDataState.exception)
            }

            else -> showGenericErrorDialog()
        }
    }

    private fun showCurrentWeatherViews(currentWeather: CurrentWeather) {
        binding.llCurrentError.visibility = GONE
        binding.tvForecast.text =
            getString(ResourceUtils.getTextForWeatherCondition(currentWeather.weather)).toUpperCase(
                Locale.getDefault()
            )
        binding.tvTemp.text = StringUtils.formatTemperature(currentWeather.currentTemp)
        binding.tvMin.text = StringUtils.formatTemperature(currentWeather.minTemp)
        binding.tvMax.text = StringUtils.formatTemperature(currentWeather.maxTemp)
        binding.tvCurrent.text = StringUtils.formatTemperature(currentWeather.currentTemp)
        binding.ivForecastBg.background = ContextCompat.getDrawable(
            requireContext(),
            ResourceUtils.getBackgroundDrawableForWeatherCondition(currentWeather.weather)
        )

        binding.forecastBg.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                ResourceUtils.getColourForWeatherCondition(currentWeather.weather)
            )
        )
        binding.llCurrentWeather.visibility = INVISIBLE

        val elapsed = "${getString(R.string.last_update)}${
            DateUtils.getElapsedTimeAsString(
                requireContext(),
                currentWeather.time
            )
        }"
        binding.tvLastUpdated.text = elapsed

        ViewAnimationUtils.fadeIn(binding.llCurrentWeather)
    }

    private fun showCurrentWeatherErrorMessage(exception: Exception) {
        binding.llCurrentError.visibility = VISIBLE
        binding.llCurrentWeather.visibility = GONE
        binding.tvCurrentError.text = getString(R.string.something_went_wrong, exception.message)
    }

    override fun onResume() {
        super.onResume()
        handleLocationPermissions()
    }

    override fun onActivityResult(result: Map<String, Boolean>?) {
        result?.let {
            for (key in result.keys) {
                if (result[key] == false) {
                    showPermissionDenied()
                    return
                }
            }
            getLastLocation(true)
        }
    }


}