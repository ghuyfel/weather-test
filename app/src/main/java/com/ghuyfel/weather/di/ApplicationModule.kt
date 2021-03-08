package com.ghuyfel.weather.di

import android.content.Context
import androidx.room.Room
import com.ghuyfel.weather.api.openweatherapi.OpenWeatherApiService
import com.ghuyfel.weather.db.Database
import com.ghuyfel.weather.db.dao.LocationDao
import com.ghuyfel.weather.db.dao.WeatherDao
import com.ghuyfel.weather.repository.Repository
import com.ghuyfel.weather.repository.mappers.CurrentWeatherMapper
import com.ghuyfel.weather.repository.mappers.LocationMapper
import com.ghuyfel.weather.repository.mappers.PlaceMapper
import com.ghuyfel.weather.repository.mappers.WeatherForecastMapper
import com.ghuyfel.weather.ui.activities.MainActivity
import com.ghuyfel.weather.utils.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.annotation.Signed

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideRetrofitBuilder(gson: Gson): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    @Provides
    @Singleton
    fun provideOpenWeatherApiService(retrofitBuilder: Retrofit.Builder): OpenWeatherApiService {
        return retrofitBuilder
            .baseUrl(Constants.BASE_URL_OPEN_WEATHER_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFusedLocation(@ApplicationContext context: Context) =
        Places.createClient(context)

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext activity: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(activity)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database =
        Room.databaseBuilder(
            context,
            Database::class.java,
            Database.DB_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideLocationDao(database: Database): LocationDao = database.getLocationDao()

    @Provides
    @Singleton
    fun provideWeatherDao(database: Database): WeatherDao = database.getWeatherDao()

    @Provides
    @Singleton
    fun provideRepository(
        openWeatherMapService: OpenWeatherApiService,
        locationDao: LocationDao,
        locationMapper: LocationMapper,
        placeMapper: PlaceMapper,
        placesClient: PlacesClient,
        fusedLocationProviderClient: FusedLocationProviderClient,
        weatherDao: WeatherDao,
        weatherForecastMapper: WeatherForecastMapper,
        currentWeatherMapper: CurrentWeatherMapper
    ): Repository {
        return Repository(
            openWeatherMapService,
            locationDao,
            locationMapper,
            placeMapper,
            placesClient,
            fusedLocationProviderClient,
            weatherDao,
            weatherForecastMapper,
            currentWeatherMapper
        )
    }

}