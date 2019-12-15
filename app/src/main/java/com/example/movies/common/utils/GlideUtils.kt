package com.example.movies.common.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

object GlideUtils {

    fun glideLoadImageOrPlaceHolder(context: Context, imageView: ImageView, url: String?) {


        url!!.let {imageUrl ->
            Glide.with(context).load((imageUrl)).into(imageView)
        }
    }
}


