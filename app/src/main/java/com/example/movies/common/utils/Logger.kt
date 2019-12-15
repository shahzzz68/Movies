package com.example.movies.common.utils

import android.util.Log
import com.example.movies.BuildConfig

object Logger {

    fun error(logCrash: Boolean, e: Exception) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace()
//            if (logCrash)
//                Crashlytics.logException(e)
//        }
        }
    }

        /**
         * Show logs only in debug mode
         * Equivalent to [Log.e]
         *
         * @param tag  tag name of log
         * @param text log text
         */
        fun error(tag: String, text: String) {
            if (BuildConfig.DEBUG)
                Log.e(tag, text)
        }

        /**
         * Show logs only in debug mode
         *
         * @param tag tag name of log
         * @param e   [Exception]
         */
        fun debug(tag: String, e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
                //  Crashlytics.logException(e)
            }
        }

        /**
         * Show logs only in debug mode
         * Equivalent to [Log.d]
         *
         * @param tag  tag name of log
         * @param text log text
         */
        fun debug(tag: String, text: String) {
            if (BuildConfig.DEBUG)
                Log.d(tag, text)
        }

        /**
         * Show logs only in debug mode
         *
         * @param tag tag name of log
         * @param e   [Exception]
         */
        fun info(tag: String, e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
                // Crashlytics.logException(e);
            }
        }

        /**
         * Show logs only in debug mode
         * Equivalent to [Log.i]
         *
         * @param tag  tag name of log
         * @param text log text
         */
        fun info(tag: String, text: String) {
            if (BuildConfig.DEBUG)
                Log.i(tag, text)
        }

        /**
         * Show logs only in debug mode
         *
         * @param tag tag name of log
         * @param e   [Exception]
         */
        fun warn(tag: String, e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
                Log.w(tag, e.toString())
            }
        }

        /**
         * Show logs only in debug mode
         * Equivalent to [Log.w]
         *
         * @param tag  tag name of log
         * @param text log text
         */
        fun warn(tag: String, text: String) {
            if (BuildConfig.DEBUG)
                Log.w(tag, text)
        }

        /**
         * Log Exception in [Crashlytics]
         *
         * @param cause [Exception]
         */
        fun caughtException(cause: Exception) {
            cause.printStackTrace()

        }

        /**
         * Get stacktrace of given exception
         *
         * @param e [Exception]
         * @return stacktrace
         */
        fun getStackTrace(e: Exception): String {
            var culprit = ""
            val elements = e.stackTrace
            for (stackTrace in elements)
                culprit = stackTrace.toString() + "\n"
            return culprit
        }

}



