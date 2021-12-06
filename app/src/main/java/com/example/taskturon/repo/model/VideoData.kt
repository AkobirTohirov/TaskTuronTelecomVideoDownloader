package com.example.taskturon.repo.model

import java.io.Serializable

data class VideoData(
    val url: String,
    val title: String?,
    val thumbnail: String?
) : Serializable