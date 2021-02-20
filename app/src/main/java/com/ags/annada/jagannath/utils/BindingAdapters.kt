package com.ags.annada.jagannath.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, imageUrl: String?) {
    Picasso.get()
        .load(imageUrl)
        .into(view);
}