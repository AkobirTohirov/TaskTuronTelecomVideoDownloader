package com.example.taskturon.repo.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        VideoEntity::class
    ], version = 1
)
abstract class MainDatabase : RoomDatabase() {
    abstract val videosDao: VideosDao
}