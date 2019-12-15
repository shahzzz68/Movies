package com.example.movies.common.utils

import android.content.Context
import android.widget.Toast

object ViewUtils {

    fun showToast(context: Context?, text: String, time: Int=Toast.LENGTH_SHORT) {

        context?.let {
            Toast.makeText(context, text, time).show()
        }

    }

}