package com.ags.annada.jagannath.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ags.annada.jagannath.datasource.models.playlist.PlaylistListItem
import com.ags.annada.jagannathauk.databinding.ItemPlaylistBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
class PlayListAdapter @ExperimentalCoroutinesApi constructor(
    private val viewModel: PlaylistListViewModel
) : ListAdapter<PlaylistListItem, PlayListAdapter.ViewHolder>(PlaylistListItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    @ExperimentalCoroutinesApi
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(viewModel, item)
    }

    class ViewHolder private constructor(val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @ExperimentalCoroutinesApi
        fun bind(viewModel: PlaylistListViewModel, item: PlaylistListItem) {
            binding.viewmodel = viewModel
            binding.item = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemPlaylistBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

class PlaylistListItemDiffCallback : DiffUtil.ItemCallback<PlaylistListItem>() {
    override fun areItemsTheSame(oldItem: PlaylistListItem, newItem: PlaylistListItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PlaylistListItem, newItem: PlaylistListItem): Boolean {
        return oldItem == newItem
    }
}
