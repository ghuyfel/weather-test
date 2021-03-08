package com.ghuyfel.weather.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ghuyfel.weather.R
import com.ghuyfel.weather.databinding.FragmentFavouritePlacesBinding
import com.ghuyfel.weather.maps.utils.MapUtils
import com.ghuyfel.weather.permissions.LocationPermissionHelper
import com.ghuyfel.weather.repository.models.FavouriteLocation
import com.ghuyfel.weather.ui.adapters.FavouriteLocationAdapter
import com.ghuyfel.weather.ui.adapters.FavouriteWeatherAdapter
import com.ghuyfel.weather.ui.events.FavouritePlacesEvents
import com.ghuyfel.weather.ui.interfaces.FavouriteLocationClickListener
import com.ghuyfel.weather.ui.models.CurrentWeather
import com.ghuyfel.weather.ui.models.ForecastWeather
import com.ghuyfel.weather.ui.models.LocationPicture
import com.ghuyfel.weather.ui.viewmodels.FavouritePlacesFragmentViewModel
import com.ghuyfel.weather.utils.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class FavouritePlacesFragment(
    private val favouriteLocationAdapter: FavouriteLocationAdapter,
    private val favouriteWeatherAdapter: FavouriteWeatherAdapter
) :
    Fragment(), ActivityResultCallback<ActivityResult>,
    OnMapReadyCallback,
    FavouriteLocationClickListener {

    companion object {
        lateinit var googleMap: GoogleMap
        val markersMap = HashMap<String, Marker>()
    }

    private val viewModel: FavouritePlacesFragmentViewModel by viewModels()

    private lateinit var binding: FragmentFavouritePlacesBinding

    private lateinit var currentLocation: FavouriteLocation

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>


    private val coordinates = ArrayList<LatLng>()

    private val locationPermissionCallback = object : ActivityResultCallback<Map<String, Boolean>> {
        override fun onActivityResult(result: Map<String, Boolean>?) {
            result?.let {
                for (key in result.keys) {
                    if (result[key] == false) {
                        showPermissionDenied()
                        return
                    }
                }
                getLocations()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
                this
            )

        locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            locationPermissionCallback
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouritePlacesBinding.inflate(layoutInflater)

        binding.fabAddPlace.setOnClickListener {
            val fields =
                listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            resultLauncher.launch(intent.build(requireActivity()))
        }

        var bundle: Bundle? = null
        savedInstanceState?.let {
            bundle = it.getBundle(Constants.MAP_VIEW_BUNDLE_KEY)
        }

        binding.mapView.onCreate(bundle)
        binding.mapView.getMapAsync(this)

        binding.rvFavLocations.setHasFixedSize(true)
        binding.rvFavLocations.layoutManager = LinearLayoutManager(requireContext())
        favouriteLocationAdapter.setListener(this)
        binding.rvFavLocations.adapter = favouriteLocationAdapter
        binding.rvFavLocations.animation = ViewAnimationUtils.fadeIn(requireContext())

        binding.placeDetails.rvForecast.setHasFixedSize(true)
        binding.placeDetails.rvForecast.layoutManager = LinearLayoutManager(requireContext())
        binding.placeDetails.rvForecast.adapter = favouriteWeatherAdapter
        binding.placeDetails.rvForecast.animation = ViewAnimationUtils.fadeIn(requireContext())

        binding.viewAll.tv.text = getString(R.string.recenter_map)
        binding.viewAll.iv.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_map)
        binding.viewAll.root.setOnClickListener { viewAllMarkers() }

        setupCurrentLocationView()

        subscribeObservers()

        getLocations()

        if (LocationPermissionHelper.shouldShowRationale(requireActivity())) {
            showPermissionRationale()
        } else if (!LocationPermissionHelper.isPermissionGranted(requireContext())) {
            LocationPermissionHelper.requestLocationPermissions(locationPermissionLauncher)
        }

        return binding.root
    }


    private fun getLocations() {
        viewModel.setEvent(FavouritePlacesEvents.GetCurrentLocation)
        viewModel.setEvent(FavouritePlacesEvents.GetFavouritePlacesLocations)
    }


    private fun subscribeObservers() {

        viewModel.requestLocationPermissionSingleEvent.observe(viewLifecycleOwner) {
            LocationPermissionHelper.requestLocationPermissions(locationPermissionLauncher)
        }

        viewModel.favouriteLocationsLiveData.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {

                is DataState.Loading -> {
                    binding.progressBar.visibility = VISIBLE
                }

                is DataState.Success -> {
                    binding.progressBar.visibility = GONE

                    favouriteLocationAdapter.submitList(dataState.data)

                    for (location in dataState.data) {
                        insertMarker(location)
                    }
                    if (coordinates.isNotEmpty()) {
                        binding.viewAll.root.visibility = VISIBLE
                        viewAllMarkers()
                    }
                }

                else -> {
                    binding.progressBar.visibility = GONE
                }
            }

        }

        viewModel.favouriteLocationInsertedLiveData.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.InsertLocation -> {
                    if(binding.viewAll.root.visibility != VISIBLE) binding.viewAll.root.visibility = VISIBLE
                    favouriteLocationAdapter.insertItem(dataState.data)
                    binding.rvFavLocations.scheduleLayoutAnimation()
                    onFavouriteLocationClicked(dataState.data)
                }
                else -> showErrorDialog(getString(R.string.failed_to_save_favourite))
            }
        }

        viewModel.deleteFavouriteLocation.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Generic -> {
                    val favouriteLocation = dataState.data
                    favouriteLocationAdapter.delete(favouriteLocation)
                    val marker = markersMap[favouriteLocation.locationId]
                    marker?.remove()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.deleted, favouriteLocation.locationName),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> showErrorDialog(getString(R.string.failed_to_delete_favourite))
            }
        }

        viewModel.weatherBulletinLocationLiveData.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Loading -> {
                    binding.placeDetails.llLoadingWeather.visibility = VISIBLE
                }

                is DataState.Generic -> {
                    binding.placeDetails.llLoadingWeather.visibility = GONE
                    handleCurrentWeatherState(dataState.data.currentWeather)
                    handleForecastState(dataState.data.forecastWeather)
                }
                else -> showErrorDialog(getString(R.string.something_went_wrong, ""))
            }
        }

        viewModel.currentLocationLiveDataState.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    val currentLocation = dataState.data
                    updateCurrentLocationView(currentLocation)
                }

                is DataState.Error -> {
                    when (dataState.exception) {
                        is PlaceNotFoundException -> {
                            viewModel.setEvent(FavouritePlacesEvents.GetCurrentFusedLocation)
                        }

                        is SecurityException -> LocationPermissionHelper.requestLocationPermissions(locationPermissionLauncher)

                        else -> {
                            binding.myLocation.root.visibility = GONE
                            val sb = StringBuilder()
                            sb.append(getString(R.string.failed_to_get_location))
                            sb.append("\n\n")
                            sb.append(dataState.exception.message)
                            showErrorDialog(sb.toString())
                        }
                    }
                }

                else -> {
                    binding.myLocation.root.visibility = GONE
                    showErrorDialog(getString(R.string.something_went_wrong, ""))
                }
            }
        }

        viewModel.favouritePlaceImageLiveData.observe(viewLifecycleOwner) { dataSet ->
            when (dataSet) {
                is DataState.Success -> {
                    updateSelectedLocationImage(dataSet.data)
                }
                else -> {
                    binding.placeDetails.llPlaceImage.visibility = GONE
                    showErrorDialog(getString(R.string.failed_to_get_location_image))
                }
            }
        }
    }

    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.error)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun updateSelectedLocationImage(data: LocationPicture) {
        binding.placeDetails.ivPlace.setImageBitmap(data.bitmap)
        binding.placeDetails.tvAttribution.text = data.attribution?.let {
            HtmlCompat.fromHtml(
                getString(
                    R.string.attribution,
                    it
                ), HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
        binding.placeDetails.tvAttribution.movementMethod =
            LinkMovementMethod.getInstance()
        binding.placeDetails.llPlaceImage.visibility = GONE
        binding.placeDetails.ivPlace.visibility = VISIBLE
        binding.placeDetails.llPlaceImage.visibility = VISIBLE
    }

    private fun updateCurrentLocationView(location: FavouriteLocation) {
        location.locationName = getString(R.string.current)
        currentLocation = location
        currentLocation.isCurrentLocation = true
        binding.llCurrentLocation.visibility = GONE
        binding.myLocation.root.visibility = VISIBLE
        insertMarker(currentLocation, true)
        MapUtils.fitBoundsForCameraTarget(googleMap, coordinates)
    }

    private fun handleCurrentWeatherState(currentWeatherDataState: DataState<CurrentWeather>) {
        when (currentWeatherDataState) {
            is DataState.Success -> {
                binding.placeDetails.tvCurrentError.visibility = GONE

                val currentWeather: CurrentWeather = currentWeatherDataState.data

                binding.placeDetails.tvForecast.text =
                    getString(ResourceUtils.getTextForWeatherCondition(currentWeather.weather)).capitalize(
                        Locale.getDefault()
                    )

                binding.placeDetails.tvTemp.text =
                    StringUtils.formatTemperature(currentWeather.currentTemp)

                val sb = StringBuilder()
                sb.append(StringUtils.formatTemperature(currentWeather.minTemp))
                sb.append("/")
                sb.append(StringUtils.formatTemperature(currentWeather.maxTemp))

                binding.placeDetails.tvTempMaxMin.text = sb.toString()
                binding.placeDetails.ivWeather.background = ContextCompat.getDrawable(
                    requireContext(),
                    ResourceUtils.getIconForWeatherCondition(currentWeather.weather)
                )
            }

            else -> {
                binding.placeDetails.tvCurrentError.visibility = VISIBLE
            }


        }
    }

    private fun handleForecastState(forecastWeather: DataState<List<ForecastWeather>>) {
        when (forecastWeather) {
            is DataState.Success -> {
                binding.placeDetails.tvForecastError.visibility = GONE
                favouriteWeatherAdapter.submitList(forecastWeather.data)
                binding.placeDetails.rvForecast.scheduleLayoutAnimation()
            }

            else -> {
                binding.placeDetails.tvForecastError.visibility = VISIBLE
            }
        }
    }

    private fun setupCurrentLocationView() {
        binding.myLocation.iv.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_my_location
            )
        )

        binding.myLocation.tv.text = getString(R.string.current_location)
        binding.myLocation.root.setOnClickListener {
            onFavouriteLocationClicked(currentLocation)
        }
        binding.myLocation.root.visibility = GONE
        binding.llCurrentLocation.visibility = VISIBLE
    }

    private fun insertMarker(location: FavouriteLocation, isCurrentLocation: Boolean = false) {
        val latLng = LatLng(location.lat, location.lng)

        val marker = MapUtils.addMarker(
            map = googleMap,
            latLng = latLng,
            name = location.locationName,
            colour = isCurrentLocation
        )

        coordinates.add(latLng)
        markersMap[location.locationId] = marker //todo move to viewModel

    }

    override fun onActivityResult(result: ActivityResult) {
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let {
                    val place = Autocomplete.getPlaceFromIntent(it)
                    handleNewPlace(place)
                }
            }
            AutocompleteActivity.RESULT_ERROR -> {
                result.data?.let {
                    val status = Autocomplete.getStatusFromIntent(it)
                    status.statusMessage?.let { message -> Log.e("TAG", message) }
                    showPlacesSelectionFailed()
                }
            }
            Activity.RESULT_CANCELED -> {
                // The user canceled the operation.
                Log.d("TAG", "User cancelled place's selection.")
            }
        }
    }

    private fun handleNewPlace(place: Place) {
        val latLng = LatLng(place.latLng?.latitude!!, place.latLng?.longitude!!)

        val marker = MapUtils.addMarker(
            map = googleMap,
            latLng = latLng,
            name = place.name ?: ""
        )

        coordinates.add(latLng)
        markersMap[place.id!!] = marker //todo move to viewModel
        MapUtils.moveCameraToCoordinates(googleMap, latLng)
        viewModel.setEvent(FavouritePlacesEvents.SaveLocation(place = place))
    }

    private fun viewAllMarkers() {
        binding.placeDetails.root.visibility = GONE
        MapUtils.fitBoundsForCameraTarget(googleMap, coordinates)
    }

    private fun showPlacesSelectionFailed() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(
                getString(
                    R.string.something_went_wrong,
                    getString(R.string.places_selection_failed)
                )
            )
    }

    private fun showPermissionDenied() {
        binding.llCurrentLocation.visibility = GONE
        showErrorDialog(getString(R.string.location_permission_denied))
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        googleMap.uiSettings?.isZoomGesturesEnabled = true
        googleMap.uiSettings?.isRotateGesturesEnabled = true
        googleMap.uiSettings?.isCompassEnabled = false
        googleMap.uiSettings?.isScrollGesturesEnabled = true
        googleMap.uiSettings?.isScrollGesturesEnabledDuringRotateOrZoom = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(Constants.MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(Constants.MAP_VIEW_BUNDLE_KEY, mapViewBundle)
        }
        binding.mapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    private fun showPermissionRationale() {
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
                    showPermissionDenied()
                    dialog.cancel()
                }
            }
            .show()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        binding.mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }


    override fun onFavouriteLocationClicked(location: FavouriteLocation) {

        binding.placeDetails.ivPlace.visibility = GONE
        binding.placeDetails.root.visibility = VISIBLE
        binding.placeDetails.tvName.text =
            if (location.isCurrentLocation) getString(R.string.current_location) else location.locationName
        binding.placeDetails.tvAddress.text = location.address
        binding.root.scrollTo(0, 0)
        favouriteWeatherAdapter.submitList(emptyList())

        MapUtils.moveCameraToCoordinates(googleMap, LatLng(location.lat, location.lng))

        viewModel.setEvent(FavouritePlacesEvents.LocationClicked(favouriteLocation = location))
    }

    override fun onDeleteButtonClicked(location: FavouriteLocation) {
        viewModel.setEvent(FavouritePlacesEvents.DeleteLocation(favouriteLocation = location))
    }

}
