package com.example.movies.common.utils

import android.content.Context
import android.content.SharedPreferences


class SharedPreferenceManager private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences
    private val editor: SharedPreferences.Editor

    init {
        sharedPreferences = context.getSharedPreferences("prefrences", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.apply()
    }

    companion object SaveValues {

        val SESSION_ID = "sessionId"
        val REQ_TOKEN_KEY = "reqToken"

        private var instance: SharedPreferenceManager? = null

//        fun singletonInstance(context: Context) {
//            synchronized(SharedPreferenceManager::class.java) {
//                if (instance == null)
//                    instance = SharedPreferenceManager(context)
//                else
//                    throw IllegalStateException("SharedPreferenceManager instance already exists.")
//            }
//        }

        fun getInstance(context: Context)=
            instance ?: synchronized(this){
                instance ?: SharedPreferenceManager(context).also { instance = it }
            }

    }

    fun save(valueKey: String, value: String) {
        editor.putString(valueKey, value)
        editor.commit()
    }

    fun clearPreferences() {
        editor.clear()
        editor.commit()
    }

    fun read(valueKey: String, valueDefault: String): String? {
        return sharedPreferences.getString(valueKey, valueDefault)
    }


    fun read(valueKey: String, valueDefault: Int): Int {
        return sharedPreferences.getInt(valueKey, valueDefault)
    }

    fun save(valueKey: String, value: Int) {
        editor.putInt(valueKey, value)
        editor.commit()
    }

    fun read(valueKey: String, valueDefault: Boolean): Boolean {
        return sharedPreferences.getBoolean(valueKey, valueDefault)
    }

    fun save(valueKey: String, value: Boolean) {
        editor.putBoolean(valueKey, value)
        editor.commit()
    }

    fun read(valueKey: String, valueDefault: Long): Long {
        return sharedPreferences.getLong(valueKey, valueDefault)
    }

    fun save(valueKey: String, value: Long) {
        editor.putLong(valueKey, value)
        editor.commit()
    }

    fun read(valueKey: String, valueDefault: Float): Float {
        return sharedPreferences.getFloat(valueKey, valueDefault)
    }

    fun save(valueKey: String, value: Float) {
        editor.putFloat(valueKey, value)
        editor.commit()
    }


}