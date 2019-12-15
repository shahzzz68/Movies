package com.example.movies.ui.main.DetailsActivity

import android.content.res.ColorStateList
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.core.content.ContextCompat
import com.example.movies.R
import com.example.movies.arch_components.room.entities.Movie
import com.example.movies.common.base.recyclerView.BaseRecyclerViewActivity
import com.example.movies.common.utils.AppConstantsUrls
import com.example.movies.common.utils.apiParms
import com.example.movies.network.HttpRequestItem
import com.example.movies.network.MyAsyncTask
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.content_movies_detail.*


open class MovieDetails : BaseRecyclerViewActivity() {

    open fun fetchMovieDetails(movieId: Int) {
        val httpRequestItem =
            HttpRequestItem(AppConstantsUrls.movieDetailsUrl(movieId))
        httpRequestItem.setParams(httpRequestItem.apiParms())
        MyAsyncTask(getProgressDialog(), this).execute(httpRequestItem)
    }

    open fun minutesToHours(totalMinutes: Int): String {
        var minutes = (totalMinutes % 60).toString()
        minutes = if (minutes.length == 1) "0$minutes" else minutes
        var hours = (totalMinutes / 60).toString()
        hours = if (hours.length == 1) "0$hours" else hours
        return "${hours}h ${minutes}min"
    }

    fun addChip(chipGroup: ChipGroup,string: String) {
        val chip = Chip(this)
        chip.apply {
            text = string
            setTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.colorPrimary)
                )
            )
            chipBackgroundColor = ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.transparentYellow)
            )
            isClickable = false
            isCheckable = false
        }

        chipGroup.apply {
            addView(chip as View)
        }

    }

    fun startAlphaAnimation(v: View, duration: Int, visibility: Int) {
        val alphaAnimation = if (visibility == View.VISIBLE)
            AlphaAnimation(0f, 1f)
        else
            AlphaAnimation(1f, 0f)

        alphaAnimation.duration = duration.toLong()
        alphaAnimation.fillAfter = true
        v.startAnimation(alphaAnimation)
    }
}