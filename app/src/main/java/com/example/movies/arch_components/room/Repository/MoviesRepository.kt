package com.example.movies.arch_components.room.Repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.movies.arch_components.room.DAO.MovieDao
import com.example.movies.arch_components.room.Database.MovieDatabase
import com.example.movies.arch_components.room.entities.Movie

class MoviesRepository(var application: Application) {

    var movieDao: MovieDao
    lateinit var retrieveAll: LiveData<List<Movie>>

    init {
        val moviesDatabase = MovieDatabase.getInstance(application)
        movieDao = moviesDatabase.movieDao()
    }

    fun retriveAll() =
        movieDao.retrieveAll()

    suspend fun insert(movie: Movie) {
        movieDao.insertMovie(movie)
    }

    fun isAdded(id: Int) =
        movieDao.isWatchListed(id)


    suspend fun delete(movie: Movie) {
        movieDao.deleteMovie(movie)
    }

    fun deleteAll() {

    }


}