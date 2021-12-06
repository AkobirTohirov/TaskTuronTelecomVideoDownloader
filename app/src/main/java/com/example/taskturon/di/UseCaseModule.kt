package com.example.taskturon.di

import com.example.taskturon.ui.screen.usecase.save_video.SaveVideoUseCase
import com.example.taskturon.ui.screen.usecase.save_video.SaveVideoUseCaseImpl
import com.example.taskturon.ui.screen.usecase.video.GetAllVideosUseCase
import com.example.taskturon.ui.screen.usecase.video.GetAllVideosUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
interface UseCaseModule {

    @Binds
    fun bindSaveVideoUseCase(binder: SaveVideoUseCaseImpl): SaveVideoUseCase

    @Binds
    fun bindGetAllVideosUseCase(binder: GetAllVideosUseCaseImpl): GetAllVideosUseCase
}