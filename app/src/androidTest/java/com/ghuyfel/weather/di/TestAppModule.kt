package com.ghuyfel.weather.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ghuyfel.weather.db.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class TestAppModule {

    @Provides
    @Named("test_db")
    fun provideInMemoryTestDb(@ApplicationContext context: Context) =
        Room.inMemoryDatabaseBuilder(
            context, Database::class.java)
            .allowMainThreadQueries()
            .build()
}