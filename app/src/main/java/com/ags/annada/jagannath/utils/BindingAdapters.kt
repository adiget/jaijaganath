package com.ags.annada.jagannath.utils

import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.ags.annada.jagannath.datasource.models.playlist.PlaylistListItem
import com.ags.annada.jagannath.home.PlayListAdapter
import com.ags.annada.jagannathauk.R
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.FlowPreview

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, imageUrl: String?) {
    Picasso.get()
        .load(imageUrl)
        .into(view)
}

@BindingAdapter("shapeableImageUrl")
fun loadShapeableImage(view: ShapeableImageView, imageUrl: String?) {
    Picasso.get()
        .load(imageUrl)
        .into(view)
}

@BindingAdapter("imageUrlPalette")
fun loadImagePalette(imageView: ImageView, imageUrl: String?) {
    if (null == imageUrl) {
        imageView.setImageResource(R.drawable.mqdefault);
    } else {
        Picasso.get()
            .load(imageUrl)
            .error(R.drawable.mqdefault)
            .transform(PaletteTransformation.instance())
            .into(imageView, object : PaletteTransformation.PaletteCallback(imageView) {
                public override fun onSuccess(palette: Palette) {
                    val color = palette.getLightVibrantColor(
                        imageView.context.resources.getColor(R.color.light_sky_blue)
                    )

                    val cardView: CardView = imageView.parent.parent as CardView
                    cardView.setCardBackgroundColor(color)
                }

                override fun onError(e: Exception?) {
                }
            })
    }
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

@BindingAdapter("visibleGone")
fun showHide(view: View, show: Boolean) {
    view.visibility = if (show) View.VISIBLE else View.GONE
}