package com.ags.annada.jagannath.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ags.annada.jagannath.datasource.models.playlist.PlaylistListItem
import com.ags.annada.jagannath.home.PlayListAdapter
import com.squareup.picasso.Picasso
import kotlinx.coroutines.FlowPreview

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, imageUrl: String?) {
    Picasso.get()
        .load(imageUrl)
        .into(view)
}

@FlowPreview
@BindingAdapter("app:playlists")
fun setPlaylists(listView: RecyclerView, items: Resource<List<PlaylistListItem>>?) {
    items?.let {
        if (it.data != null) {
            (listView.adapter as PlayListAdapter).submitList(it.data)
        }
    }
}