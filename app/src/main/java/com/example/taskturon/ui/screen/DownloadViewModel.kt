package com.example.taskturon.ui.screen

import com.example.taskturon.base.BaseViewModel
import com.example.taskturon.repo.db.VideoEntity
import com.example.taskturon.ui.screen.usecase.save_video.SaveVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    val save: SaveVideoUseCase
) : BaseViewModel() {
    fun saveVideo(video: VideoEntity) = launch { save(video) }
}