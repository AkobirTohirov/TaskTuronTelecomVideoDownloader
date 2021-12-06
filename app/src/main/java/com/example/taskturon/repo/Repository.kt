package com.example.taskturon.repo

import com.example.taskturon.repo.db.VideoEntity
import com.example.taskturon.repo.db.VideosDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Repository @Inject constructor(
    private val videosDao: VideosDao
) {
    fun getAllVideos(): Flow<List<VideoEntity>> = videosDao.getAll()

    suspend fun saveVideo(video: VideoEntity): Long =
        withContext(Dispatchers.IO) { videosDao.insert(video) }

    suspend fun saveVideo(videos: List<VideoEntity>): List<Long> =
        withContext(Dispatchers.IO) { videosDao.insert(videos) }
}