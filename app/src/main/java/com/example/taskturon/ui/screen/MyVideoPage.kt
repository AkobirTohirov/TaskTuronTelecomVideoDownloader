package com.example.taskturon.ui.screen

import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.example.taskturon.R
import com.example.taskturon.base.BaseFragment
import com.example.taskturon.repo.db.VideoEntity
import com.example.taskturon.ui.BottomNavScreenDirections
import com.example.taskturon.ui.screen.adapter.VideosAdapter
import com.example.taskturon.utils.extensions.activityNavController
import com.example.taskturon.utils.extensions.decodeAndCopyTo
import com.example.taskturon.utils.extensions.getCurrentTimeStamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.page_downloads.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class MyVideosPage : BaseFragment(R.layout.page_downloads) {

    override val viewModel: MyVideosViewModel by viewModels()
    override val navController: NavController by activityNavController(R.id.navHostFragment)

    private val videosAdapter: VideosAdapter by lazy { VideosAdapter() }

    override fun initialize() {
        rvVideos.adapter = videosAdapter

        videosAdapter.onItemClickListener = { videoUrl ->
            val dir = BottomNavScreenDirections.globalOpenMedia(videoUrl)
            viewModel.navigateTo(dir)
        }

        viewModel.videosLiveData.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                videosAdapter.submitList(it)
                decodeAllVideos(it)
            }
        }
    }

    private fun decodeAllVideos(videos: List<VideoEntity>) {
        GlobalScope.launch(Dispatchers.IO) {
            videos.forEach {
                val filename = "${getCurrentTimeStamp()}${it.title}.mp4"
                val out = File(requireContext().cacheDir, filename)
                val input = File(it.localUrl)
                input.decodeAndCopyTo(out)
                it.decodedUrl = out.absolutePath
            }

            launch(Dispatchers.Main) {
                videosAdapter.submitList(videos)
                videosAdapter.notifyDataSetChanged()
            }

        }
    }
}