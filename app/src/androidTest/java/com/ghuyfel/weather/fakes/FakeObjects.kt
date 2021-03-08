package com.ghuyfel.weather.fakes

import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Parcel
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.ghuyfel.weather.R
import com.ghuyfel.weather.api.openweatherapi.models.responses.GetCurrentWeatherResponse
import com.ghuyfel.weather.api.openweatherapi.models.responses.GetForecastWeatherResponse
import com.ghuyfel.weather.db.entities.CurrentWeatherEntity
import com.ghuyfel.weather.db.entities.ForecastWeatherEntity
import com.ghuyfel.weather.db.entities.LocationEntity
import com.ghuyfel.weather.repository.models.FavouriteLocation
import com.ghuyfel.weather.repository.models.WeatherBulletin
import com.ghuyfel.weather.ui.models.CurrentWeather
import com.ghuyfel.weather.ui.models.ForecastWeather
import com.ghuyfel.weather.ui.models.LocationPicture
import com.ghuyfel.weather.utils.DataState
import com.ghuyfel.weather.utils.DateUtils
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Response

object FakeObjects {

    fun getForecastWeatherEntityList() = listOf(
        ForecastWeatherEntity(
            locationId = "someLocationId",
            locationName = "some location name",
            dayOfTheWeek = "Monday",
            weather = "Sunny",
            temp = 1.1,
            time = 123456L
        ),
        ForecastWeatherEntity(
            locationId = "someLocationId1",
            locationName = "some location name1",
            dayOfTheWeek = "Wednesday",
            weather = "Sunny",
            temp = 1.1,
            time = 123456L
        ),
        ForecastWeatherEntity(
            locationId = "someLocationId2",
            locationName = "some location name 2",
            dayOfTheWeek = "Thursday",
            weather = "Cloudy",
            temp = 1.1,
            time = 123456L
        ),
        ForecastWeatherEntity(
            locationId = "someLocationId3",
            locationName = "some location name 3",
            dayOfTheWeek = "Friday",
            weather = "rainy",
            temp = 1.1,
            time = 123456L
        )
    )

    fun getForecastWeatherEntity() = ForecastWeatherEntity(
        locationId = "someLocationId4",
        locationName = "some location name 4",
        dayOfTheWeek = "Saturday",
        weather = "Sunny",
        temp = 1.1,
        time = 123456L
    )

    fun getCurrentWeatherEntity() = CurrentWeatherEntity(
        locationId = "someLocationId",
        locationName = "some location name",
        weather = "sunny",
        minTemp = 1.0,
        maxTemp = 1.0,
        time = 123456L,
        currentTemp = 1.0
    )

    fun getLocationEntity() = LocationEntity(
        locationId = "someLocationId",
        name = "Some name",
        lat = 1.1,
        lng = 1.1,
        address = "some address"
    )

    fun getLocationEntityList() = listOf(
        LocationEntity(
            locationId = "someLocationId",
            name = "Some location name",
            lat = 1.1,
            lng = 1.1,
            address = "Some address"
        ),
        LocationEntity(
            locationId = "someLocationId1",
            name = "Some name 1",
            lat = 1.1,
            lng = 1.1,
            address = "some address 1"
        ),
        LocationEntity(
            locationId = "someLocationId2",
            name = "Some name 2",
            lat = 1.1,
            lng = 1.1,
            address = "some address 2"
        ),
        LocationEntity(
            locationId = "someLocationId3",
            name = "Some name 3",
            lat = 1.1,
            lng = 1.1,
            address = "some address 3"
        )
    )

    fun getFavouriteLocation() = FavouriteLocation(
        locationId = "someLocationId",
        locationName = "Some location name",
        lat = 1.1,
        lng = 1.1,
        address = "Some address"
    )

    fun getWeatherBulletin() = WeatherBulletin(
        currentWeather = DataState.Success(
            CurrentWeather(
                locationId = "someLocationId",
                locationName = "some location name",
                weather = "sunny",
                minTemp = 1.0,
                maxTemp = 1.0,
                time = 123456L,
                currentTemp = 1.0
            )
        ),

        forecastWeather = DataState.Success(
            listOf(
                ForecastWeather(
                    locationId = "someLocationId",
                    locationName = "some location name",
                    dayOfTheWeek = "Monday",
                    weather = "Sunny",
                    temp = 1.1,
                    time = 123456L
                ),
                ForecastWeather(
                    locationId = "someLocationId1",
                    locationName = "some location name1",
                    dayOfTheWeek = "Wednesday",
                    weather = "Sunny",
                    temp = 1.1,
                    time = 123456L
                )
            )
        )
    )

    fun getNullLocationPicture() = LocationPicture(
        bitmap = null,
        attribution = null
    )

    fun getFavouriteLocationsList(): List<FavouriteLocation> = listOf(
        FavouriteLocation(
            locationId = "someLocationId1",
            locationName = "Some name 1",
            lat = 1.1,
            lng = 1.1,
            address = "some address 1"
        ),
        FavouriteLocation(
            locationId = "someLocationId2",
            locationName = "Some name 2",
            lat = 1.1,
            lng = 1.1,
            address = "some address 2"
        )

    )

    fun getPlace(): Place {
        return FakePlace(
            locationId = "fakePlaceId",
            placeName =  "Some fake place",
            lat = 2.2,
            lng = 2.2,
            placeAddress = "Some fake place address"
        )
    }


    class FakePlace(
        val locationId: String,
        val placeName: String,
        val lat: Double,
        val lng: Double,
        val placeAddress: String?
    ) : Place() {
        override fun describeContents(): Int = 0

        override fun writeToParcel(dest: Parcel?, flags: Int) {}

        override fun getAddress(): String? = placeAddress

        override fun getAddressComponents(): AddressComponents? = null

        override fun getBusinessStatus(): BusinessStatus? = null

        override fun getAttributions(): MutableList<String> = mutableListOf("Some attribution")

        override fun getId(): String = locationId

        override fun getLatLng(): LatLng = LatLng(lat, lng)

        override fun getName(): String = placeName

        override fun getOpeningHours(): OpeningHours? = null

        override fun getPhoneNumber(): String? = null

        override fun getPhotoMetadatas(): MutableList<PhotoMetadata> = getPlacePhotoMetaData()

        override fun getPlusCode(): PlusCode? = null

        override fun getPriceLevel(): Int? = null

        override fun getRating(): Double? = null

        override fun getTypes(): MutableList<Type> = mutableListOf()

        override fun getUserRatingsTotal(): Int? = null

        override fun getUtcOffsetMinutes(): Int? = null

        override fun getViewport(): LatLngBounds? = null

        override fun getWebsiteUri(): Uri? = null

    }

    fun getCurrentWeatherSuccessResponse(): Response<GetCurrentWeatherResponse> =
         Response.success(getCurrentWeatherResponse())


    fun getCurrentWeather(): CurrentWeather {
        val currentWeatherResponse = getCurrentWeatherResponse()
        return CurrentWeather(
            currentTemp = currentWeatherResponse.main.temp,
            maxTemp = currentWeatherResponse.main.temp_max,
            minTemp = currentWeatherResponse.main.temp_min,
            weather = currentWeatherResponse.weather[0].main,
            time = currentWeatherResponse.dt.toLong(),
            locationId = "someLocationId",
            locationName = "Some location name",
        )
    }

    fun getCurrentWeatherResponse(): GetCurrentWeatherResponse {
        val responseString = "{\"coord\":{\"lon\":28.0436,\"lat\":-26.2023},\"weather\":[{\"id\":801,\"main\":\"Clouds\",\"description\":\"few clouds\",\"icon\":\"02d\"}],\"base\":\"stations\",\"main\":{\"temp\":297.99,\"feels_like\":295.94,\"temp_min\":296.48,\"temp_max\":300.37,\"pressure\":1016,\"humidity\":31,\"sea_level\":1016,\"grnd_level\":833},\"visibility\":10000,\"wind\":{\"speed\":1.79,\"deg\":81,\"gust\":2.24},\"clouds\":{\"all\":15},\"dt\":1614683281,\"sys\":{\"type\":3,\"id\":2007136,\"country\":\"ZA\",\"sunrise\":1614657734,\"sunset\":1614703083},\"timezone\":7200,\"id\":993800,\"name\":\"Johannesburg\",\"cod\":200}"
        return Gson().fromJson(responseString, GetCurrentWeatherResponse::class.java)
    }

    fun getForecastWeatherResponse(): GetForecastWeatherResponse {
        val responseString = "{\"cod\":\"200\",\"message\":0,\"cnt\":40,\"list\":[{\"dt\":1614697200,\"main\":{\"temp\":296.88,\"feels_like\":293.97,\"temp_min\":296.88,\"temp_max\":296.91,\"pressure\":1014,\"sea_level\":1014,\"grnd_level\":831,\"humidity\":33,\"temp_kf\":-0.03},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03d\"}],\"clouds\":{\"all\":30},\"wind\":{\"speed\":3,\"deg\":166},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2021-03-02 15:00:00\"},{\"dt\":1614708000,\"main\":{\"temp\":295.03,\"feels_like\":292.84,\"temp_min\":294.53,\"temp_max\":295.03,\"pressure\":1016,\"sea_level\":1016,\"grnd_level\":832,\"humidity\":35,\"temp_kf\":0.5},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03n\"}],\"clouds\":{\"all\":34},\"wind\":{\"speed\":1.73,\"deg\":200},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2021-03-02 18:00:00\"},{\"dt\":1614718800,\"main\":{\"temp\":292.98,\"feels_like\":290.34,\"temp_min\":292.72,\"temp_max\":292.98,\"pressure\":1017,\"sea_level\":1017,\"grnd_level\":832,\"humidity\":40,\"temp_kf\":0.26},\"weather\":[{\"id\":801,\"main\":\"Clouds\",\"description\":\"few clouds\",\"icon\":\"02n\"}],\"clouds\":{\"all\":16},\"wind\":{\"speed\":2.41,\"deg\":212},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2021-03-02 21:00:00\"},{\"dt\":1614729600,\"main\":{\"temp\":290.77,\"feels_like\":288.73,\"temp_min\":290.72,\"temp_max\":290.77,\"pressure\":1017,\"sea_level\":1017,\"grnd_level\":830,\"humidity\":47,\"temp_kf\":0.05},\"weather\":[{\"id\":801,\"main\":\"Clouds\",\"description\":\"few clouds\",\"icon\":\"02n\"}],\"clouds\":{\"all\":19},\"wind\":{\"speed\":1.66,\"deg\":177},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2021-03-03 00:00:00\"},{\"dt\":1614740400,\"main\":{\"temp\":288.99,\"feels_like\":287.48,\"temp_min\":288.99,\"temp_max\":288.99,\"pressure\":1017,\"sea_level\":1017,\"grnd_level\":829,\"humidity\":56,\"temp_kf\":0},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01n\"}],\"clouds\":{\"all\":0},\"wind\":{\"speed\":1.18,\"deg\":106},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2021-03-03 03:00:00\"},{\"dt\":1614751200,\"main\":{\"temp\":291.8,\"feels_like\":290.47,\"temp_min\":291.8,\"temp_max\":291.8,\"pressure\":1017,\"sea_level\":1017,\"grnd_level\":831,\"humidity\":49,\"temp_kf\":0},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"clouds\":{\"all\":0},\"wind\":{\"speed\":1.14,\"deg\":74},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2021-03-03 06:00:00\"},{\"dt\":1614762000,\"main\":{\"temp\":297.98,\"feels_like\":295.08,\"temp_min\":297.98,\"temp_max\":297.98,\"pressure\":1014,\"sea_level\":1014,\"grnd_level\":832,\"humidity\":28,\"temp_kf\":0},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"clouds\":{\"all\":0},\"wind\":{\"speed\":2.55,\"deg\":152},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2021-03-03 09:00:00\"},{\"dt\":1614772800,\"main\":{\"temp\":300.58,\"feels_like\":296.87,\"temp_min\":300.58,\"temp_max\":300.58,\"pressure\":1011,\"sea_level\":1011,\"grnd_level\":831,\"humidity\":21,\"temp_kf\":0},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"clouds\":{\"all\":0},\"wind\":{\"speed\":3.2,\"deg\":136},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2021-03-03 12:00:00\"},{\"dt\":1614783600,\"main\":{\"temp\":299.31,\"feels_like\":295.76,\"temp_min\":299.31,\"temp_max\":299.31,\"pressure\":1009,\"sea_level\":1009,\"grnd_level\":829,\"humidity\":24,\"temp_kf\":0},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03d\"}],\"clouds\":{\"all\":30},\"wind\":{\"speed\":3.19,\"deg\":163},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2021-03-03 15:00:00\"},{\"dt\":1614794400,\"main\":{\"temp\":296.5,\"feels_like\":294.06,\"temp_min\":296.5,\"temp_max\":296.5,\"pressure\":1012,\"sea_level\":1012,\"grnd_level\":829,\"humidity\":29,\"temp_kf\":0},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03n\"}],\"clouds\":{\"all\":31},\"wind\":{\"speed\":1.68,\"deg\":166},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2021-03-03 18:00:00\"},{\"dt\":1614805200,\"main\":{\"temp\":294.89,\"feels_like\":291.69,\"temp_min\":294.89,\"temp_max\":294.89,\"pressure\":1014,\"sea_level\":1014,\"grnd_level\":830,\"humidity\":33,\"temp_kf\":0},\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04n\"}],\"clouds\":{\"all\":93},\"wind\":{\"speed\":2.9,\"deg\":113},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2021-03-03 21:00:00\"},{\"dt\":1614816000,\"main\":{\"temp\":291.98,\"feels_like\":288.77,\"temp_min\":291.98,\"temp_max\":291.98,\"pressure\":1013,\"sea_level\":1013,\"grnd_level\":828,\"humidity\":43,\"temp_kf\":0},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04n\"}],\"clouds\":{\"all\":64},\"wind\":{\"speed\":3.27,\"deg\":73},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2021-03-04 00:00:00\"},{\"dt\":1614826800,\"main\":{\"temp\":290.9,\"feels_like\":288.67,\"temp_min\":290.9,\"temp_max\":290.9,\"pressure\":1014,\"sea_level\":1014,\"grnd_level\":828,\"humidity\":58,\"temp_kf\":0},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01n\"}],\"clouds\":{\"all\":4},\"wind\":{\"speed\":3.01,\"deg\":34},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2021-03-04 03:00:00\"},{\"dt\":1614837600,\"main\":{\"temp\":292.84,\"feels_like\":291.23,\"temp_min\":292.84,\"temp_max\":292.84,\"pressure\":1014,\"sea_level\":1014,\"grnd_level\":829,\"humidity\":54,\"temp_kf\":0},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"clouds\":{\"all\":2},\"wind\":{\"speed\":2.41,\"deg\":29},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2021-03-04 06:00:00\"},{\"dt\":1614848400,\"main\":{\"temp\":299.27,\"feels_like\":297.46,\"temp_min\":299.27,\"temp_max\":299.27,\"pressure\":1012,\"sea_level\":1012,\"grnd_level\":831,\"humidity\":29,\"temp_kf\":0},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"clouds\":{\"all\":0},\"wind\":{\"speed\":1.49,\"deg\":34},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2021-03-04 09:00:00\"},{\"dt\":1614859200,\"main\":{\"temp\":301,\"feels_like\":299.42,\"temp_min\":301,\"temp_max\":301,\"pressure\":1009,\"sea_level\":1009,\"grnd_level\":829,\"humidity\":22,\"temp_kf\":0},\"weather\":[{\"id\":801,\"main\":\"Clouds\",\"description\":\"few clouds\",\"icon\":\"02d\"}],\"clouds\":{\"all\":18},\"wind\":{\"speed\":0.42,\"deg\":160},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2021-03-04 12:00:00\"},{\"dt\":1614870000,\"main\":{\"temp\":298.15,\"feels_like\":295.44,\"temp_min\":298.15,\"temp_max\":298.15,\"pressure\":1008,\"sea_level\":1008,\"grnd_level\":827,\"humidity\":28,\"temp_kf\":0},\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"clouds\":{\"all\":87},\"wind\":{\"speed\":2.32,\"deg\":222},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2021-03-04 15:00:00\"},{\"dt\":1614880800,\"main\":{\"temp\":296.17,\"feels_like\":294.47,\"temp_min\":296.17,\"temp_max\":296.17,\"pressure\":1011,\"sea_level\":1011,\"grnd_level\":828,\"humidity\":33,\"temp_kf\":0},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04n\"}],\"clouds\":{\"all\":82},\"wind\":{\"speed\":1.08,\"deg\":53},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2021-03-04 18:00:00\"},{\"dt\":1614891600,\"main\":{\"temp\":294.73,\"feels_like\":293.43,\"temp_min\":294.73,\"temp_max\":294.73,\"pressure\":1013,\"sea_level\":1013,\"grnd_level\":829,\"humidity\":37,\"temp_kf\":0},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04n\"}],\"clouds\":{\"all\":68},\"wind\":{\"speed\":0.62,\"deg\":119},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2021-03-04 21:00:00\"},{\"dt\":1614902400,\"main\":{\"temp\":293.14,\"feels_like\":290.63,\"temp_min\":293.14,\"temp_max\":293.14,\"pressure\":1013,\"sea_level\":1013,\"grnd_level\":828,\"humidity\":46,\"temp_kf\":0},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04n\"}],\"clouds\":{\"all\":55},\"wind\":{\"speed\":2.92,\"deg\":55},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2021-03-05 00:00:00\"},{\"dt\":1614913200,\"main\":{\"temp\":292.26,\"feels_like\":290.36,\"temp_min\":292.26,\"temp_max\":292.26,\"pressure\":1014,\"sea_level\":1014,\"grnd_level\":829,\"humidity\":44,\"temp_kf\":0},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04n\"}],\"clouds\":{\"all\":62},\"wind\":{\"speed\":1.58,\"deg\":27},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2021-03-05 03:00:00\"},{\"dt\":1614924000,\"main\":{\"temp\":293.5,\"feels_like\":290.57,\"temp_min\":293.5,\"temp_max\":293.5,\"pressure\":1015,\"sea_level\":1015,\"grnd_level\":830,\"humidity\":42,\"temp_kf\":0},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"clouds\":{\"all\":51},\"wind\":{\"speed\":3.19,\"deg\":33},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2021-03-05 06:00:00\"},{\"dt\":1614934800,\"main\":{\"temp\":299.39,\"feels_like\":297.1,\"temp_min\":299.39,\"temp_max\":299.39,\"pressure\":1013,\"sea_level\":1013,\"grnd_level\":832,\"humidity\":27,\"temp_kf\":0},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"clouds\":{\"all\":7},\"wind\":{\"speed\":1.89,\"deg\":346},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2021-03-05 09:00:00\"},{\"dt\":1614945600,\"main\":{\"temp\":299.66,\"feels_like\":298.07,\"temp_min\":299.66,\"temp_max\":299.66,\"pressure\":1011,\"sea_level\":1011,\"grnd_level\":830,\"humidity\":24,\"temp_kf\":0},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03d\"}],\"clouds\":{\"all\":35},\"wind\":{\"speed\":0.47,\"deg\":177},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2021-03-05 12:00:00\"},{\"dt\":1614956400,\"main\":{\"temp\":298.44,\"feels_like\":296.25,\"temp_min\":298.44,\"temp_max\":298.44,\"pressure\":1010,\"sea_level\":1010,\"grnd_level\":829,\"humidity\":27,\"temp_kf\":0},\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"clouds\":{\"all\":99},\"wind\":{\"speed\":1.51,\"deg\":152},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2021-03-05 15:00:00\"},{\"dt\":1614967200,\"main\":{\"temp\":296.57,\"feels_like\":295.2,\"temp_min\":296.57,\"temp_max\":296.57,\"pressure\":1013,\"sea_level\":1013,\"grnd_level\":830,\"humidity\":30,\"temp_kf\":0},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04n\"}],\"clouds\":{\"all\":82},\"wind\":{\"speed\":0.3,\"deg\":218},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2021-03-05 18:00:00\"},{\"dt\":1614978000,\"main\":{\"temp\":294.92,\"feels_like\":291.7,\"temp_min\":294.92,\"temp_max\":294.92,\"pressure\":1015,\"sea_level\":1015,\"grnd_level\":831,\"humidity\":40,\"temp_kf\":0},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04n\"}],\"clouds\":{\"all\":79},\"wind\":{\"speed\":3.79,\"deg\":84},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2021-03-05 21:00:00\"},{\"dt\":1614988800,\"main\":{\"temp\":293.61,\"feels_like\":292.41,\"temp_min\":293.61,\"temp_max\":293.61,\"pressure\":1016,\"sea_level\":1016,\"grnd_level\":831,\"humidity\":46,\"temp_kf\":0},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04n\"}],\"clouds\":{\"all\":69},\"wind\":{\"speed\":1.21,\"deg\":37},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2021-03-06 00:00:00\"},{\"dt\":1614999600,\"main\":{\"temp\":291.85,\"feels_like\":289.76,\"temp_min\":291.85,\"temp_max\":291.85,\"pressure\":1017,\"sea_level\":1017,\"grnd_level\":831,\"humidity\":55,\"temp_kf\":0},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03n\"}],\"clouds\":{\"all\":26},\"wind\":{\"speed\":2.85,\"deg\":3},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2021-03-06 03:00:00\"},{\"dt\":1615010400,\"main\":{\"temp\":292.7,\"feels_like\":289.67,\"temp_min\":292.7,\"temp_max\":292.7,\"pressure\":1018,\"sea_level\":1018,\"grnd_level\":832,\"humidity\":52,\"temp_kf\":0},\"weather\":[{\"id\":801,\"main\":\"Clouds\",\"description\":\"few clouds\",\"icon\":\"02d\"}],\"clouds\":{\"all\":13},\"wind\":{\"speed\":4.17,\"deg\":359},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2021-03-06 06:00:00\"},{\"dt\":1615021200,\"main\":{\"temp\":298.1,\"feels_like\":295.37,\"temp_min\":298.1,\"temp_max\":298.1,\"pressure\":1016,\"sea_level\":1016,\"grnd_level\":834,\"humidity\":35,\"temp_kf\":0},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"clouds\":{\"all\":0},\"wind\":{\"speed\":3.38,\"deg\":326},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2021-03-06 09:00:00\"},{\"dt\":1615032000,\"main\":{\"temp\":299.84,\"feels_like\":297.68,\"temp_min\":299.84,\"temp_max\":299.84,\"pressure\":1013,\"sea_level\":1013,\"grnd_level\":832,\"humidity\":27,\"temp_kf\":0},\"weather\":[{\"id\":801,\"main\":\"Clouds\",\"description\":\"few clouds\",\"icon\":\"02d\"}],\"clouds\":{\"all\":19},\"wind\":{\"speed\":1.82,\"deg\":271},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2021-03-06 12:00:00\"},{\"dt\":1615042800,\"main\":{\"temp\":297.13,\"feels_like\":295.62,\"temp_min\":297.13,\"temp_max\":297.13,\"pressure\":1013,\"sea_level\":1013,\"grnd_level\":831,\"humidity\":36,\"temp_kf\":0},\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"clouds\":{\"all\":90},\"wind\":{\"speed\":1.49,\"deg\":325},\"visibility\":10000,\"pop\":0.05,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2021-03-06 15:00:00\"},{\"dt\":1615053600,\"main\":{\"temp\":294.09,\"feels_like\":292.8,\"temp_min\":294.09,\"temp_max\":294.09,\"pressure\":1016,\"sea_level\":1016,\"grnd_level\":831,\"humidity\":50,\"temp_kf\":0},\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10n\"}],\"clouds\":{\"all\":85},\"wind\":{\"speed\":1.96,\"deg\":101},\"visibility\":10000,\"pop\":0.37,\"rain\":{\"3h\":0.5},\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2021-03-06 18:00:00\"},{\"dt\":1615064400,\"main\":{\"temp\":294.06,\"feels_like\":293.26,\"temp_min\":294.06,\"temp_max\":294.06,\"pressure\":1017,\"sea_level\":1017,\"grnd_level\":832,\"humidity\":46,\"temp_kf\":0},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04n\"}],\"clouds\":{\"all\":75},\"wind\":{\"speed\":0.78,\"deg\":27},\"visibility\":10000,\"pop\":0.1,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2021-03-06 21:00:00\"},{\"dt\":1615075200,\"main\":{\"temp\":292.67,\"feels_like\":291.26,\"temp_min\":292.67,\"temp_max\":292.67,\"pressure\":1016,\"sea_level\":1016,\"grnd_level\":831,\"humidity\":51,\"temp_kf\":0},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04n\"}],\"clouds\":{\"all\":63},\"wind\":{\"speed\":1.74,\"deg\":67},\"visibility\":10000,\"pop\":0.05,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2021-03-07 00:00:00\"},{\"dt\":1615086000,\"main\":{\"temp\":291.92,\"feels_like\":290.25,\"temp_min\":291.92,\"temp_max\":291.92,\"pressure\":1016,\"sea_level\":1016,\"grnd_level\":830,\"humidity\":55,\"temp_kf\":0},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03n\"}],\"clouds\":{\"all\":35},\"wind\":{\"speed\":2.27,\"deg\":308},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2021-03-07 03:00:00\"},{\"dt\":1615096800,\"main\":{\"temp\":294.71,\"feels_like\":292.76,\"temp_min\":294.71,\"temp_max\":294.71,\"pressure\":1017,\"sea_level\":1017,\"grnd_level\":832,\"humidity\":43,\"temp_kf\":0},\"weather\":[{\"id\":801,\"main\":\"Clouds\",\"description\":\"few clouds\",\"icon\":\"02d\"}],\"clouds\":{\"all\":18},\"wind\":{\"speed\":2.27,\"deg\":303},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2021-03-07 06:00:00\"},{\"dt\":1615107600,\"main\":{\"temp\":299.6,\"feels_like\":296.07,\"temp_min\":299.6,\"temp_max\":299.6,\"pressure\":1014,\"sea_level\":1014,\"grnd_level\":833,\"humidity\":29,\"temp_kf\":0},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"clouds\":{\"all\":8},\"wind\":{\"speed\":4.04,\"deg\":240},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2021-03-07 09:00:00\"},{\"dt\":1615118400,\"main\":{\"temp\":299.97,\"feels_like\":297.03,\"temp_min\":299.97,\"temp_max\":299.97,\"pressure\":1011,\"sea_level\":1011,\"grnd_level\":831,\"humidity\":25,\"temp_kf\":0},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03d\"}],\"clouds\":{\"all\":50},\"wind\":{\"speed\":2.63,\"deg\":214},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2021-03-07 12:00:00\"}],\"city\":{\"id\":993800,\"name\":\"Johannesburg\",\"coord\":{\"lat\":-26.2023,\"lon\":28.0436},\"country\":\"ZA\",\"population\":2026469,\"timezone\":7200,\"sunrise\":1614657734,\"sunset\":1614703083}}"
        return Gson().fromJson(responseString, GetForecastWeatherResponse::class.java)
    }

    fun getForecastWeatherSuccessResponse(): Response<GetForecastWeatherResponse> =
         Response.success(getForecastWeatherResponse())

    fun getForecastWeather(): List<ForecastWeather> {
        val response = getForecastWeatherResponse()
        val daysOfTheWeek = ArrayList<String>()
        val uniqueDays = ArrayList<ForecastWeather>()

        for (forecast in response.list) {
            val day = DateUtils.getDayOfTheWeekFromMilliseconds(forecast.dt * 1000)
            if (!daysOfTheWeek.contains(day)) {
                val forecastWeather = ForecastWeather(
                    dayOfTheWeek = day,
                    weather = forecast.weather[0].main,
                    temp = forecast.main.temp,
                    time = forecast.dt,
                    locationId = "someLocationId",
                    locationName = "Some location name",
                )
                daysOfTheWeek.add(day)
                uniqueDays.add(forecastWeather)
            }
        }

        return uniqueDays
    }

    fun getCurrentWeatherFailureResponse(): Response<GetCurrentWeatherResponse> =
        Response.error(404, ResponseBody.create(null,""))

    fun getForecastWeatherFailureResponse(): Response<GetForecastWeatherResponse> =
        Response.error(404, ResponseBody.create(null,""))

    fun getLocationPicture(context: Context): LocationPicture {
        return LocationPicture(
            attribution = "Some attributions",
            bitmap = ContextCompat.getDrawable(context, R.drawable.ic_image)?.toBitmap()
        )
    }

    fun getPlacePhotoMetaData() = mutableListOf(PhotoMetadata
        .builder("somedata").apply {
            attributions = "Some Attributions"
            height = 100
            width = 100
        }.build())

    fun getLocation(): Location {
        return object: Location("some provider") {
            override fun getLatitude(): Double = 2.2
            override fun getLongitude(): Double = 2.2
        }
    }

    fun getPlaceLikelihoods(): MutableList<PlaceLikelihood> = mutableListOf(
        PlaceLikelihood.newInstance(getPlace(), 0.82),
        PlaceLikelihood.newInstance(getPlace(), 0.6)
    )

    fun getFindCurrentPlaceResponse(): FindCurrentPlaceResponse? =
        FindCurrentPlaceResponse.newInstance(getPlaceLikelihoods())

    class FakeFetchPhotoResponse(val context: Context): FetchPhotoResponse() {
        override fun getBitmap(): Bitmap {
            return ContextCompat.getDrawable(context, R.drawable.ic_image)?.toBitmap()!!
        }

    }

    class FakeFetchPlaceResponse: FetchPlaceResponse() {
        val metadatas = listOf(PhotoMetadata
        .builder("somedata").apply {
            attributions = "Some Attributions"
            height = 100
            width = 100
        }.build())

        override fun getPlace(): Place {
            return FakeObjects.getPlace()
        }


    }
}