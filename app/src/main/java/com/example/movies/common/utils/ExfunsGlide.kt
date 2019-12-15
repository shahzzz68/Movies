package com.example.movies.common.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

fun Context.loadImage(imageView: ImageView, url: String?) {

    url!!.let {imageUrl ->
        Glide.with(this).load((imageUrl)).into(imageView)
    }
}