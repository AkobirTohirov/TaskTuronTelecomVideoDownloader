package com.example.taskturon.ui.media

import android.net.Uri
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.taskturon.R
import com.example.taskturon.base.BaseFragment
import com.example.taskturon.base.BaseViewModel
import com.example.taskturon.utils.extensions.gone
import com.example.taskturon.utils.extensions.visible
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.fragment_player_controller.*
import kotlinx.android.synthetic.main.screen_open_media.*

class OpenMediaScreen : BaseFragment(R.layout.screen_open_media) {

    private var player: SimpleExoPlayer? = null
    private lateinit var playerView: PlayerView

    private val args: OpenMediaScreenArgs by navArgs()

    override val viewModel: BaseViewModel by viewModels()

    private var url: String? = null

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    override fun initialize() {
        playerView = requireView().findViewById(R.id.video_view)
        url = args.itemUrl


        if (url != null) {
            bottomBar.visible()
            initializePlayer()
        }

        btBack.setOnClickListener {
            viewModel.back()
        }
    }


    override fun onClick(p0: View?) {

    }

    private fun initializePlayer(){

        player = SimpleExoPlayer.Builder(requireContext()).build()
        playerView.player = player

        val media: MediaSource

        val bandWidthMeter = DefaultBandwidthMeter()

        val videoTrackSelectionFactory: TrackSelection.Factory =
            AdaptiveTrackSelection.Factory()

        val extractorsFactory = DefaultExtractorsFactory()

        val mediaDataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            requireContext(), Util.getUserAgent(
                requireContext(), getString(R.string.app_name)
            )
        )

        media = ExtractorMediaSource(
            Uri.parse(url!!),
            mediaDataSourceFactory, extractorsFactory, null, null
        )

        player!!.addListener(object : Player.EventListener {

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_BUFFERING) {
                    pbPlayer.visible()
                } else if (playbackState == Player.STATE_READY) {
                    pbPlayer.gone()
                }
                super.onPlayerStateChanged(playWhenReady, playbackState)
            }

        })

        player!!.playWhenReady = true
        player!!.setMediaSource(media)
        player?.seekTo(currentWindow, playbackPosition)
        player!!.prepare()
    }

    fun pausePlayer() {
        player?.playWhenReady = false
    }


    private fun releasePlayer() {
        if (player != null) {
            playWhenReady = player!!.playWhenReady
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            player!!.playWhenReady = false
            player!!.release()
            player!!.stop()
            player = null
        }
    }

    override fun onDestroyView() {
        releasePlayer()
        super.onDestroyView()
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    override fun onPause() {
        releasePlayer()
        super.onPause()
    }

    override fun onStop() {
        releasePlayer()
        super.onStop()
    }
}