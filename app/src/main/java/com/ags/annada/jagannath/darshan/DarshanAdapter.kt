package com.ags.annada.jagannath.darshan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ags.annada.jagannath.datasource.models.playlistItem.PlaylistItem
import com.ags.annada.jagannathauk.databinding.ItemDarshanBinding


class DarshanAdapter(
    private val viewModel: DarshanViewModel
) : ListAdapter<PlaylistItem, DarshanAdapter.ViewHolder>(DarshanDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(viewModel, item)
    }

    class ViewHolder private constructor(val binding: ItemDarshanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: DarshanViewModel, item: PlaylistItem) {
            binding.viewmodel = viewModel
            binding.item = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDarshanBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

class DarshanDiffCallback : DiffUtil.ItemCallback<PlaylistItem>() {
    override fun areItemsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
        return oldItem == newItem
    }
}
