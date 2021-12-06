package com.example.taskturon.ui.screen.usecase.save_video

import com.example.taskturon.repo.Repository
import com.example.taskturon.repo.db.VideoEntity
import javax.inject.Inject

class SaveVideoUseCaseImpl @Inject constructor(
    private val repository: Repository
) : SaveVideoUseCase {
    override suspend fun invoke(video: VideoEntity): Long = repository.saveVideo(video)
}