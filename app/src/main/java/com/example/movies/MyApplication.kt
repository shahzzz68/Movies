package com.example.movies

import android.app.Application
import com.example.movies.arch_components.room.Database.MovieDatabase
import com.example.movies.common.utils.SharedPreferenceManager

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        //SharedPreferenceManager.singletonInstance(applicationContext)
        MovieDatabase.getInstance(applicationContext)
    }
}