package com.example.taskturon.di

import android.content.Context
import androidx.room.Room
import com.example.taskturon.repo.db.MainDatabase
import com.example.taskturon.repo.db.VideosDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
open class DBModule {

    @Provides
    @Singleton
    open fun getRoomDatabase(
        @ApplicationContext context: Context
    ): MainDatabase = Room.databaseBuilder(
        context.applicationContext,
        MainDatabase::class.java,
        "VideoDatabase"
    ).build()

    @Provides
    @Singleton
    open fun getCategoryDao(db: MainDatabase): VideosDao = db.videosDao
}