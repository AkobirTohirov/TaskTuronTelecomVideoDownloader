package com.example.taskturon.ui.screen.usecase.save_video

import com.example.taskturon.repo.db.VideoEntity

interface SaveVideoUseCase {
    suspend operator fun invoke(video: VideoEntity): Long
}