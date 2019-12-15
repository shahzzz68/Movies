package com.example.movies.arch_components.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.movies.common.interfaces.BaseItem
import com.example.movies.common.utils.ViewTypeConstants

@Entity(tableName = "movie_table")
class Movie(

    @PrimaryKey @ColumnInfo(name = "movie_id") var movieId: Int,
    var title: String,
    var poster_path: String,
    var release_date: String,
    var popularity: Double,
    var vote_count: Int,
    var vote_average: Double,
    var isSelected: Boolean
) : BaseItem {


    override fun getItemType(): Int {
        return ViewTypeConstants.MOVIE_MODEL_DB
    }
}
