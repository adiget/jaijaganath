package com.ags.annada.jagannath.di

import android.content.Context
import androidx.room.Room
import com.ags.annada.jagannath.datasource.room.JaiJaganathDatabase
import com.ags.annada.jagannath.datasource.room.daos.PlaylistListDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun providePlaylistDao(appDatabase: JaiJaganathDatabase): PlaylistListDao {
        return appDatabase.playlistListDao()
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): JaiJaganathDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            JaiJaganathDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO
}

const val DATABASE_NAME = "JaiJaganath.db"