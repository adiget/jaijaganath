package com.ags.annada.jagannath.home

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ags.annada.jagannath.datasource.models.playlist.PlaylistListItem
import com.ags.annada.jagannath.main.MainFragmentDirections
import com.ags.annada.jagannath.utils.EventObserver
import com.ags.annada.jagannathauk.R
import com.ags.annada.jagannathauk.databinding.FragmentPlaylistBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import timber.log.Timber

@AndroidEntryPoint
class PlayListFragment : Fragment() {
    @ExperimentalCoroutinesApi
    @FlowPreview
    private val viewModel by viewModels<PlaylistListViewModel>()

    @FlowPreview
    private lateinit var adapter: PlayListAdapter

    private lateinit var binding: FragmentPlaylistBinding

    @ExperimentalCoroutinesApi
    @FlowPreview
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = this.viewLifecycleOwner

        setupSnackbar()
        setupNavigation()
        setupAdapter()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)

        val item = menu.findItem(R.id.action_search)
        val searchView: SearchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                //viewModel.setSearchString(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                //viewModel.setSearchString(newText)
                return false
            }
        })
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    private fun setupAdapter() {
        val viewModel = binding.viewmodel

        if (viewModel != null) {
            adapter = PlayListAdapter(viewModel)
            binding.playlistList.adapter = adapter
        } else {
            Timber.d("ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun setupSnackbar() {
//        viewModel.snackbarText.observe(viewLifecycleOwner, EventObserver {
//            context?.getString(it)
//                ?.let { view?.let { it1 -> Snackbar.make(it1, it, Snackbar.LENGTH_SHORT).show() } }
//        })
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    private fun setupNavigation() {
        viewModel.selectItemEvent.observe(viewLifecycleOwner, EventObserver { it ->
            Timber.d("SELECTED ID selected=${it}")

            val id = it
            val item: PlaylistListItem? = viewModel.items.value?.data?.first { it.id == id }

            openDetails(it, item?.snippet?.title)
        })
    }

    private fun openDetails(playlistId: String, title: String?) {
        val action =
            MainFragmentDirections.actionMainFragmentToDarshanFragment(playlistId, title ?: "")
        findNavController().navigate(action)
    }

    companion object {
        @JvmStatic
        fun newInstance(channelId: String) =
            PlayListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CHANNEL_ID, channelId)
                }
            }
    }
}

const val ARG_CHANNEL_ID = "channelId"