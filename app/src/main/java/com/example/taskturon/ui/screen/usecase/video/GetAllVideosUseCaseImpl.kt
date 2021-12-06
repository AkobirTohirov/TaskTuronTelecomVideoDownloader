package com.example.taskturon.ui.screen.usecase.video

import com.example.taskturon.repo.Repository
import com.example.taskturon.repo.db.VideoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllVideosUseCaseImpl @Inject constructor(
    private val repository: Repository
) : GetAllVideosUseCase {
    override fun invoke(): Flow<List<VideoEntity>> = repository.getAllVideos()
}