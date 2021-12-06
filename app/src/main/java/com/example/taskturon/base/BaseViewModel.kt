package com.example.taskturon.base

import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.example.taskturon.utils.SingleEventLiveData
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

open class BaseViewModel : ViewModel() {

    val navigate = SingleEventLiveData<NavCommand>()

    fun navigateTo(direction: NavDirections) {
        navigate.postValue(NavCommand.To(direction))
    }

    fun back() {
        navigate.postValue(NavCommand.BACK)
    }

    protected fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job = viewModelScope.launch(context, start, block)

    fun isUrlReachable(url: String) = liveData(Dispatchers.IO) {
        val connectionUrl = URL(url)
        try {
            val connection = connectionUrl.openConnection() as HttpURLConnection
            emit(connection.responseCode == 200)
        } catch (e: Exception) {
            emit(false)
        }
    }

    fun getVideoDuration(videoUrl: String) = liveData(Dispatchers.IO) {
        var duration = 0L
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(videoUrl)
            val time =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            duration = time?.toLong() ?: 0
        } catch (e: Exception) {

        } finally {
            retriever.release()
            retriever.close()
        }
        emit(duration)
    }


}