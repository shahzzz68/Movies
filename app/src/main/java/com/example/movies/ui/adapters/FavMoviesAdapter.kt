package com.example.movies.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.movies.R
import com.example.movies.arch_components.room.entities.Movie
import com.example.movies.common.base.recyclerView.BaseRecyclerViewAdapter
import com.example.movies.common.base.recyclerView.BaseRecyclerViewHolder
import com.example.movies.common.interfaces.BaseItem
import com.example.movies.common.interfaces.ItemTouchInterface
import com.example.movies.common.interfaces.RecyclerViewClickListener
import com.example.movies.common.utils.AppConstantsUrls
import com.example.movies.common.utils.RecyclerDiffUtils
import com.example.movies.common.utils.ViewTypeConstants
import com.example.movies.common.utils.loadImage
import com.example.movies.models.PopularModel
import kotlinx.android.synthetic.main.row_fav_movies.view.*


class FavMoviesAdapter(
    var context: Context,
    override var items: MutableList<BaseItem>?,
    override var recyclerViewClickListener: RecyclerViewClickListener
) : BaseRecyclerViewAdapter(items, recyclerViewClickListener) {

    override fun createSpecificViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseRecyclerViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_fav_movies, parent, false)
        return FavMoviesViewHolder(v)
    }

    override fun bindSpecificViewHolder(holder: BaseRecyclerViewHolder, position: Int) {
        when (holder) {
            is FavMoviesViewHolder -> {
                val modelDB = getItemAt(position) as PopularModel
                holder.bindPopular(modelDB)
            }

        }

    }

    inner class FavMoviesViewHolder(var v: View) : BaseRecyclerViewHolder(v, true) {

        override fun populateView(): BaseRecyclerViewHolder {
            return this
        }


        fun bindPopular(popularModel: PopularModel) {
            val posterpath = popularModel.poster_path
            if (posterpath.equals(AppConstantsUrls.NO_IMAGE_URL))
                context.loadImage(v.iv_fav_image, AppConstantsUrls.NO_IMAGE_URL)
            else
                context.loadImage(v.iv_fav_image, AppConstantsUrls.getPosterPath(posterpath))
            v.tv_fav_title.text = popularModel.title
            v.tv_fav_realease_date.text = popularModel.release_date
            v.tv_vote_count.text = popularModel.vote_count.toString()
            v.fav_ratingBar.rating = (popularModel.vote_average.toFloat()) / 2
            v.tv_popularity.text = popularModel.popularity.toString()

            toggleCheck(adapterPosition, v.iv_fav_image)
            //setAnimation(v,adapterPosition)
        }

        override fun onClick(p0: View?) {
            recyclerViewClickListener.onRecyclerViewChildItemClick(this, p0!!.id, v.iv_fav_image)
        }

        override fun onLongClick(p0: View?): Boolean {
            recyclerViewClickListener.onRecyclerViewLongClick(this, p0!!.id)
            return true
        }


    }



    fun updateMovieListItems(movie: MutableList<BaseItem>) {
        val diffCallback = RecyclerDiffUtils(items!!, movie)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items!!.clear()
        items!!.addAll(movie)
        diffResult.dispatchUpdatesTo(this)
    }

}