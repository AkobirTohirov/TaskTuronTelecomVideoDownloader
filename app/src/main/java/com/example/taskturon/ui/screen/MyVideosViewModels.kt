package com.example.taskturon.ui.screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.taskturon.base.BaseViewModel
import com.example.taskturon.repo.db.VideoEntity
import com.example.taskturon.ui.screen.usecase.video.GetAllVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyVideosViewModel @Inject constructor(
      getAllVideos: GetAllVideosUseCase
) : BaseViewModel() {

    val videosLiveData: LiveData<List<VideoEntity>> = getAllVideos().asLiveData()
}