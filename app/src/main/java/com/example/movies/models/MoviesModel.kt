package com.example.movies.models

import com.example.movies.common.interfaces.BaseItem
import com.example.movies.common.utils.ViewTypeConstants

data class MoviesModel(
    var adult: Boolean,
    var backdropPath: String,
    var genreIds: List<Int>,
    var id: Int,
    var originalLanguage: String,
    var originalTitle: String,
    var overview: String,
    var popularity: Double,
    var poster_path: String?,
    var release_date: String?,
    var title: String,
    var video: Boolean,
    var vote_average: Double,
    var vote_count: Int,
    var selected: Boolean =false
) : BaseItem {

    companion object{
        var isGrid: Boolean = false
    }

    override fun getItemType(): Int {
        return ViewTypeConstants.ITEM_MOVIES
    }
}




