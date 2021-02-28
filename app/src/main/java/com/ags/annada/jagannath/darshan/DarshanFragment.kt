package com.ags.annada.jagannath.darshan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.ags.annada.jagannath.main.MainFragment
import com.ags.annada.jagannath.main.MainFragmentDirections
import com.ags.annada.jagannath.utils.EventObserver
import com.ags.annada.jagannath.utils.Result
import com.ags.annada.jagannathauk.databinding.FragmentDarshansBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class DarshanFragment : Fragment() {
    private val viewModel by viewModels<DarshanViewModel>()
    private lateinit var adapter: DarshanAdapter

    private lateinit var binding: FragmentDarshansBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDarshansBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = this.viewLifecycleOwner

        setupSnackbar()
        setupNavigation()
        setupAdapter()
        setupObserver()
    }

    private fun setupAdapter() {
        val viewModel = binding.viewmodel

        if (viewModel != null) {
            adapter = DarshanAdapter(viewModel)
            binding.darshansList.adapter = adapter
        } else {
            Timber.d("ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun setupSnackbar() {
        viewModel.snackbarText.observe(viewLifecycleOwner, EventObserver {
            context?.getString(it)
                ?.let { view?.let { it1 -> Snackbar.make(it1, it, Snackbar.LENGTH_SHORT).show() } }
        })
    }

    private fun setupNavigation() {
        viewModel.selectItemEvent.observe(viewLifecycleOwner, EventObserver { it ->
            Timber.d("Selected Video id selected=${it}")

            val videoId = it

            openDetails(
                videoId,
                viewModel.selectedPlayListItem?.snippet?.title ?: "",
                viewModel.selectedPlayListItem?.snippet?.description ?: ""
            )
        })
    }

    fun setupObserver() {
        viewModel.getPlaylistItems()
        viewModel.playlistItemLiveData.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Result.Success -> {
                    hideProgressBar()
                    response.data.let {
                        adapter.submitList(it.playlistItem2s.filter {
                            it.status.privacyStatus.equals(
                                "public"
                            )
                        }.sortedByDescending { it.snippet.publishedAt })
                    }
                }

                is Result.Error -> {
                    hideProgressBar()
                }

                is Result.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        //progressBarVideoList.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        //progressBarVideoList.visibility = View.VISIBLE
    }

    private fun openDetails(videoId: String, title: String, description: String) {

        val action: NavDirections = if (parentFragment is MainFragment) {
            MainFragmentDirections.actionMainFragmentToDarshanPlayerFragment(
                videoId,
                title,
                description
            )
        } else {
            DarshanFragmentDirections.actionDarshanFragmentToDarshanPlayerFragment(
                videoId,
                title,
                description
            )
        }
        findNavController().navigate(action)
    }

    companion object {
        @JvmStatic
        fun newInstance(playlistId: String) =
            DarshanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PLAYLIST_ID, playlistId)
                }
            }
    }
}

const val ARG_PLAYLIST_ID = "playlistId"