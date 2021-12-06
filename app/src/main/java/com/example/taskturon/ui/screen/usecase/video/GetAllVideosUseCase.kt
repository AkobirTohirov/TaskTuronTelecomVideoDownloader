package com.example.taskturon.ui.screen.usecase.video

import com.example.taskturon.repo.db.VideoEntity
import kotlinx.coroutines.flow.Flow

interface GetAllVideosUseCase {
    operator fun invoke(): Flow<List<VideoEntity>>
}