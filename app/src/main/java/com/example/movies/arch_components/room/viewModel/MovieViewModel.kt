package com.example.movies.arch_components.room.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.movies.arch_components.room.Repository.MoviesRepository
import com.example.movies.arch_components.room.entities.Movie
import kotlinx.coroutines.launch

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    var moviesRepository: MoviesRepository = MoviesRepository(application)
    var allMovies: LiveData<List<Movie>>

    init {
        allMovies = moviesRepository.retriveAll()
    }

    fun insertMovie(movie: Movie) {
        viewModelScope.launch {
            moviesRepository.insert(movie)
        }
    }

    fun deleteMovie(movie: Movie) {
        viewModelScope.launch {
            moviesRepository.delete(movie)
        }
    }

    fun isAdded(id: Int) =
        moviesRepository.isAdded(id)

}