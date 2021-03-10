package com.ags.annada.jagannath.mangalaarti

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
import com.ags.annada.jagannathauk.databinding.FragmentMangalaAratiBinding
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragmentX
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_darshan_player.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import timber.log.Timber

@AndroidEntryPoint
class MangalaAratiFragment : Fragment() {
    @ExperimentalCoroutinesApi
    @FlowPreview
    private val viewModel by viewModels<MangalaAratiViewModel>()
    private lateinit var binding: FragmentMangalaAratiBinding
    var youTubePlayerFragment: YouTubePlayerSupportFragmentX? = null

    @ExperimentalCoroutinesApi
    @FlowPreview
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMangalaAratiBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }

        return binding.root
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = this.viewLifecycleOwner
        video_description?.movementMethod = ScrollingMovementMethod()

        setupObserver()
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    private fun setupObserver() {
        viewModel.videoId?.observe(viewLifecycleOwner, {
            viewModel.getVideoStatistics(it)

            it?.let { it1 -> initYouTube(it1) }
        })

        viewModel.shareVideoEvent.observe(viewLifecycleOwner, Observer {
            shareVideo()
        })

        viewModel.rateVideoEvent.observe(viewLifecycleOwner, Observer {
            rateVideo()
        })
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
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

        Timber.d("initYouTube VideoId:- $videoId")

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

                    Timber.d("onInitializationSuccess")
                    if (videoId != null) {
                        if (wasRestored) {
                            Timber.d("wasRestored")
                            player.play()
                        } else {
                            Timber.d("loadVideo")
                            player.loadVideo(videoId)
                        }
                    }
                }

                override fun onInitializationFailure(
                    provider: YouTubePlayer.Provider,
                    youTubeInitializationResult: YouTubeInitializationResult
                ) {
                    Timber.d("onInitializationFailure")
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

    companion object {
        @JvmStatic
        fun newInstance(playlistId: String) =
            MangalaAratiFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PLAYLIST_ID, playlistId)
                }
            }
    }
}

const val ARG_PLAYLIST_ID = "playlistId"
const val RECOVERY_DIALOG_REQUEST = 100