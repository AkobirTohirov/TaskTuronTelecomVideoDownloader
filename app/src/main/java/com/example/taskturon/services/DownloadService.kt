package com.example.taskturon.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.example.taskturon.R
import com.example.taskturon.repo.Repository
import com.example.taskturon.repo.db.VideoEntity
import com.example.taskturon.repo.model.*
import com.example.taskturon.ui.MainActivity
import com.example.taskturon.utils.Const.ACTION_PAUSE_DOWNLOADING
import com.example.taskturon.utils.Const.ACTION_SHOW_DOWNLOADING_SCREEN
import com.example.taskturon.utils.Const.ACTION_START_DOWNLOADING
import com.example.taskturon.utils.Const.ACTION_STOP_DOWNLOADING
import com.example.taskturon.utils.Const.NOTIFICATION_CHANNEL_ID
import com.example.taskturon.utils.Const.NOTIFICATION_CHANNEL_NAME
import com.example.taskturon.utils.Const.NOTIFICATION_ID
import com.example.taskturon.utils.Const.VIDEO_DATA
import com.example.taskturon.utils.extensions.encodeAndCopyTo
import com.example.taskturon.utils.extensions.externalOfflineDir
import com.example.taskturon.utils.extensions.getCurrentTimeStamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class DownloadService : LifecycleService() {

    @Inject
    lateinit var repository: Repository

    override fun onCreate() {
        super.onCreate()
        notificationManager = NotificationManagerCompat.from(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (intent.action) {

                ACTION_START_DOWNLOADING -> {
                    video = intent.extras?.getSerializable(VIDEO_DATA) as VideoData?
                    if (video != null) {
                        if (status is Canceled) {
                            startForegroundService(video!!)
                        } else if (status is Paused) {
                            resumeDownloading()
                        }
                    }
                }

                ACTION_PAUSE_DOWNLOADING -> {
                    pauseDownloading()
                }

                ACTION_STOP_DOWNLOADING -> {
                    cancelService("")
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService(video: VideoData) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        startForeground(NOTIFICATION_ID, getNotificationBuilder().build())
        startDownloading(video.url)
        setObservers()
        status = Downloading(0)
    }

    private fun resumeDownloading() {
        PRDownloader.resume(downloadingId)
        status = Downloading(0)

    }

    private fun setObservers() {
        downloadingStatus.observe(this) {
            if (it is Downloading) {
                val builder = getNotificationBuilder()
                    .setContentText("${it.percentage} %")
                    .setProgress(100, it.percentage, false)
                notificationManager.notify(NOTIFICATION_ID, builder.build())
            }
        }
    }

    private fun cancelService(reason: String = "success") {
        PRDownloader.cancel(downloadingId)
        notificationManager.cancel(NOTIFICATION_ID)
        stopSelf()
        status = Canceled(reason)
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {

        val pauseIntent =
            PendingIntent.getService(this, 1, Intent(this, DownloadService::class.java).apply {
                action = ACTION_PAUSE_DOWNLOADING
            }, PendingIntent.FLAG_UPDATE_CURRENT)

        val stopIntent =
            PendingIntent.getService(this, 1, Intent(this, DownloadService::class.java).apply {
                action = ACTION_STOP_DOWNLOADING
            }, PendingIntent.FLAG_UPDATE_CURRENT)

        val resumeIntent =
            PendingIntent.getService(this, 1, Intent(this, DownloadService::class.java).apply {
                action = ACTION_START_DOWNLOADING
            }, PendingIntent.FLAG_UPDATE_CURRENT)

        var title = video?.title ?: "title of video"
        if (title.length >= 35) {
            title = "...${title.drop(title.length - 35)}"
        }

        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setColor(ContextCompat.getColor(this, R.color.colorRed))
            .setSmallIcon(R.drawable.ic_download)
            .setContentTitle(title)
            .setProgress(100, 0, false)
            .setContentText("0 %")
            .addAction(R.drawable.exo_icon_pause, "Cancel", stopIntent)

            .setContentIntent(getPendingMainActivityPendingIntent())

        if (status is Downloading) {
            builder.addAction(R.drawable.exo_icon_pause, "Pause", pauseIntent)
        } else {
            builder.addAction(R.drawable.exo_icon_pause, "Resume", resumeIntent)
        }

        return builder
    }

    private fun startDownloading(fileUrl: String) {
        fileUrl.let {
            val filePath = getDir("filesdir", Context.MODE_PRIVATE).absolutePath
            val fileName = "offline_video${getCurrentTimeStamp()}.pdf"

            if (it.isNotEmpty()) {
                downloadingId = PRDownloader.download(it, filePath, fileName).build()
                    .setOnStartOrResumeListener { }
                    .setOnProgressListener { progress ->
                        CoroutineScope(Dispatchers.Main).launch {
                            status =
                                Downloading(((progress.currentBytes * 100) / progress.totalBytes).toInt())
                            delay(1000L)
                        }
                    }
                    .start(object : OnDownloadListener {

                        override fun onDownloadComplete() {
                            val f = "$filePath/$fileName"
                            downloadingFile = File(f)
                            completeDownloading(downloadingFile!!)
                        }

                        override fun onError(error: Error?) {
                            status = Canceled(error?.serverErrorMessage ?: "Not found")
                            PRDownloader.cancel(downloadingId)
                            notificationManager.cancel(NOTIFICATION_ID)
                            stopSelf()
                        }

                    })

            }
        }
    }

    private fun completeDownloading(downloadedFile: File) {
        val timeStamp = getCurrentTimeStamp()
        val file = File(externalOfflineDir, timeStamp)

        CoroutineScope(Dispatchers.Main).launch {
            downloadedFile.encodeAndCopyTo(file)
            status = Success(file)
            repository.saveVideo(
                VideoEntity(
                    remoteUrl = video!!.url,
                    localUrl = file.absolutePath,
                    title = video!!.title ?: file.name,
                    duration = 0,
                )
            )
            delay(1000L)
            cancelService()
        }
    }

    private fun pauseDownloading() {
        PRDownloader.pause(downloadingId)
        status = Paused
    }


    private fun getPendingMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_DOWNLOADING_SCREEN
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManagerCompat) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        val downloadingStatus = MutableLiveData<Status>()
    }

    private lateinit var notificationManager: NotificationManagerCompat
    private var video: VideoData? = null
    private var downloadingId = -1
    private var downloadingFile: File? = null
    private var status: Status = Canceled("not started yet")
        private set(value) {
            downloadingStatus.postValue(value)
            field = value
        }
}