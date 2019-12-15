package com.example.movies.arch_components.room.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.movies.arch_components.room.DAO.MovieDao
import com.example.movies.arch_components.room.entities.Movie


@Database(entities = [Movie::class], version = 4)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    companion object {

        private var instance: MovieDatabase? = null

        fun getInstance(context: Context): MovieDatabase {
            return instance ?: synchronized(this)
            {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    MovieDatabase::class.java,
                    "database"
                ).fallbackToDestructiveMigration()
                    .allowMainThreadQueries().build().also { instance = it }
            }
        }

        fun destroyInstance() {
            instance = null
        }
    }

}