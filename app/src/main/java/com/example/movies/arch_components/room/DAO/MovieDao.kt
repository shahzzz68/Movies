package com.example.movies.arch_components.room.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.movies.arch_components.room.entities.Movie
import com.example.movies.common.interfaces.BaseItem

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovie(movie:Movie)

    @Delete
    suspend fun deleteMovie(movie:Movie)

    @Update
    fun updateMovie(movie:Movie)

    @Query("DELETE FROM movie_table ")
    fun deleteAll()

    @Query("SELECT * FROM movie_table")
    fun retrieveAll() : LiveData<List<Movie>>

    @Query("SELECT EXISTS(SELECT isSelected FROM movie_table WHERE movie_id =:movieId LIMIT 1 )")
    fun isWatchListed(movieId: Int) : LiveData<Boolean>


}