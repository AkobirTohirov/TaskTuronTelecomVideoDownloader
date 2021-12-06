package com.example.taskturon.ui.screen

import android.content.Intent
import android.util.Log
import android.util.Patterns
import android.webkit.URLUtil
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.example.taskturon.R
import com.example.taskturon.base.BaseFragment
import com.example.taskturon.repo.model.*
import com.example.taskturon.services.DownloadService
import com.example.taskturon.ui.BottomNavScreenDirections
import com.example.taskturon.utils.Const.ACTION_PAUSE_DOWNLOADING
import com.example.taskturon.utils.Const.ACTION_START_DOWNLOADING
import com.example.taskturon.utils.Const.ACTION_STOP_DOWNLOADING
import com.example.taskturon.utils.Const.VIDEO_DATA
import com.example.taskturon.utils.extensions.activityNavController
import com.example.taskturon.utils.extensions.gone
import com.example.taskturon.utils.extensions.stringText
import com.example.taskturon.utils.extensions.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.page_download.*
import java.util.*

@AndroidEntryPoint
class DownloadScreen : BaseFragment(R.layout.page_download) {
    private var videoTitle = ""
    private var videoUrl = ""

    override val viewModel: DownloadViewModel by viewModels()
    override val navController: NavController by activityNavController(R.id.navHostFragment)

    override fun initialize() {
        showDetails()
        btCheck.setOnClickListener {
            videoUrl = inputUrl.stringText
            videoTitle = URLUtil.guessFileName(videoUrl, null, null)
            if (videoTitle.isEmpty()) videoTitle = "no title video"

            hideKeyboard()
            if (checkUrl(videoUrl)) {
                isLoading = true
                viewModel.isUrlReachable(videoUrl).observe(viewLifecycleOwner) { reachable ->
                    isLoading = false
                    if (reachable) {
                        showDetails()
                        sendCommandToService(
                            ACTION_START_DOWNLOADING,
                            VideoData(videoUrl, videoTitle, null)
                        )
                    } else {
                        message("URL is not reachable")
                    }
                }
            }
        }

        DownloadService.apply {
            downloadingStatus.observe(viewLifecycleOwner) { status ->
                if (status != null) {
                    when (status) {
                        is Downloading -> {
                            llVideo.visible()
                            btCheck.gone()
                            inputUrl.gone()
                            tvPercentage.text = ("${status.percentage}%")
                            pbDownloading.progress = status.percentage
                        }
                        is Canceled -> {
                            llVideo.gone()
                            btCheck.visible()
                            inputUrl.visible()
                            inputUrl.setText("")
                            message(status.message)
                        }
                        is Success -> {}
                        Paused -> {
                        }
                    }
                }
            }
        }

        btCancel.setOnClickListener {
            sendCommandToService(ACTION_STOP_DOWNLOADING)
        }

        btPause.setOnClickListener {
            sendCommandToService(ACTION_PAUSE_DOWNLOADING)
        }

        btResume.setOnClickListener {
            sendCommandToService(ACTION_START_DOWNLOADING)
        }

        btPlay.setOnClickListener {
            val dir = BottomNavScreenDirections.globalOpenMedia(videoUrl)
            viewModel.navigateTo(dir)
        }
    }


    private fun showDetails() {
        Glide.with(requireView())
            .load(videoUrl)
            .error(R.color.colorGreyB3)
            .placeholder(R.color.colorGreyB3)
            .into(ivThumbnail)

        tvTitle.text = videoTitle
        viewModel.getVideoDuration(videoUrl).observe(viewLifecycleOwner) {
            tvDuration.text = getVideoDurationString(it)
        }
    }

    override fun onDestroyView() {
        DownloadService.downloadingStatus.value = null
        super.onDestroyView()
    }

    private fun getVideoDurationString(duration: Long): String {
        var seconds = duration / 1000
        val hours = seconds / 3600
        seconds %= 3600
        val minutes = seconds / 60
        seconds %= 60
        val hoursStr = if (hours > 9) hours.toString() else "0$hours"
        val minutesStr = if (minutes > 9) minutes.toString() else "0$minutes"
        val secondsStr = if (seconds > 9) seconds.toString() else "0$seconds"

        return if (hours != 0L) {
            "$hoursStr:$minutesStr:$secondsStr"
        } else
            "$minutesStr:$secondsStr"
    }

    private fun checkUrl(url: String): Boolean {
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            message("URL is not valid")
            return false
        }

        if (!url.toLowerCase(Locale.getDefault()).endsWith(".mp4")) {
            message("there is no mp4 file in this url")
            return false
        }
        return true
    }

    private fun sendCommandToService(command: String, videoData: VideoData? = null) {
        Intent(requireContext(), DownloadService::class.java).also {
            it.action = command
            it.putExtra(VIDEO_DATA, videoData)
            requireContext().startService(it)
        }
    }

}