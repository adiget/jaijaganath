package com.ags.annada.jagannath.darshan.details

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ags.annada.jagannath.datasource.network.api.Contracts.Companion.API_KEY
import com.ags.annada.jagannathauk.R
import com.ags.annada.jagannathauk.databinding.FragmentDarshanPlayerBinding
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragmentX
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_darshan_player.*

@AndroidEntryPoint
class DarshanPlayerFragment : Fragment() {
    private val viewModel by viewModels<DarshanPlayerViewModel>()
    private lateinit var binding: FragmentDarshanPlayerBinding
    var youTubePlayerFragment: YouTubePlayerSupportFragmentX? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDarshanPlayerBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }

        initYouTube(viewModel.videoId)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = this.viewLifecycleOwner
        video_description?.movementMethod = ScrollingMovementMethod()

        setupObserver()
    }

    private fun setupObserver() {
        viewModel.getVideoStatistics()

        viewModel.shareVideoEvent.observe(viewLifecycleOwner, Observer {
            shareVideo()
        })

        viewModel.rateVideoEvent.observe(viewLifecycleOwner, Observer {
            rateVideo()
        })
    }

    private fun shareVideo() {
        val shareVideoIntent = Intent()
        shareVideoIntent.action = Intent.ACTION_SEND
        shareVideoIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            "Watch \"" + viewModel.videoTitle + "\" on YouTube"
        )
        shareVideoIntent.putExtra(
            Intent.EXTRA_TEXT,
            "https://www.youtube.com/watch?v=" + viewModel.videoId
        )
        shareVideoIntent.type = "text/plain"
        startActivity(shareVideoIntent)
    }

    private fun rateVideo() {
        Toast.makeText(
            context,
            "To rate this video, please click YouTube link on Video",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun initYouTube(videoId: String?) {
        youTubePlayerFragment =
            childFragmentManager.findFragmentById(R.id.darshanPlayer) as YouTubePlayerSupportFragmentX?

        youTubePlayerFragment?.initialize(
            API_KEY,
            object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(
                    provider: YouTubePlayer.Provider,
                    player: YouTubePlayer,
                    wasRestored: Boolean
                ) {
                    player.fullscreenControlFlags =
                        YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION

                    if (videoId != null) {
                        if (wasRestored) {
                            player.play()
                        } else {
                            player.loadVideo(videoId)
                        }
                    }
                }

                override fun onInitializationFailure(
                    provider: YouTubePlayer.Provider,
                    youTubeInitializationResult: YouTubeInitializationResult
                ) {
                    if (youTubeInitializationResult.isUserRecoverableError) {
                        youTubeInitializationResult.getErrorDialog(
                            activity,
                            RECOVERY_DIALOG_REQUEST
                        ).show()
                    } else {
                        if (isAdded) {
                            Toast.makeText(
                                activity?.applicationContext,
                                getString(R.string.error_init_failure_msg),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            })
    }
}

const val KEY_VIDEO_ID = "videoId"
const val KEY_VIDEO_TITLE = "videoTitle"
const val KEY_VIDEO_DESCRIPTION = "videoDescription"
const val RECOVERY_DIALOG_REQUEST = 100