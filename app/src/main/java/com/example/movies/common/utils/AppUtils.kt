package com.example.movies.common.utils

object AppUtils {

    fun ifNotNullEmpty(text: String?): Boolean {
        return text != null && text.isNotEmpty()
    }
}