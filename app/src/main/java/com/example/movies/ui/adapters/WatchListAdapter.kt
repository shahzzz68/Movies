package com.example.movies.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.movies.R
import com.example.movies.arch_components.room.entities.Movie
import com.example.movies.common.base.recyclerView.BaseRecyclerViewAdapter
import com.example.movies.common.base.recyclerView.BaseRecyclerViewHolder
import com.example.movies.common.interfaces.BaseItem
import com.example.movies.common.interfaces.RecyclerViewClickListener
import com.example.movies.common.utils.AppConstantsUrls
import com.example.movies.common.utils.loadImage
import kotlinx.android.synthetic.main.row_fav_movies.view.*

class WatchListAdapter(
    var context: Context,
    override var items: MutableList<BaseItem>?,
    override var recyclerViewClickListener: RecyclerViewClickListener
) : BaseRecyclerViewAdapter(items, recyclerViewClickListener) {

    lateinit var itemTouchHelper: ItemTouchHelper


    override fun createSpecificViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseRecyclerViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_fav_movies, parent, false)
        return WatchListViewHolder(v)
    }

    override fun bindSpecificViewHolder(holder: BaseRecyclerViewHolder, position: Int) {
        when(holder)
        {
            is WatchListViewHolder -> {
                val model = getItemAt(position) as Movie
                holder.bindMovieDatabase(model)
            }
        }

    }

    fun setTouchHelper(itemTouchHelper: ItemTouchHelper)
    {
        this.itemTouchHelper = itemTouchHelper
    }

    inner class WatchListViewHolder(var v: View) : BaseRecyclerViewHolder(v,true) {
        override fun populateView(): BaseRecyclerViewHolder {
            return this
        }

        fun bindMovieDatabase(movie: Movie) {
            val posterpath = movie.poster_path
            if (posterpath.equals(AppConstantsUrls.NO_IMAGE_URL))
                context.loadImage(v.iv_fav_image, AppConstantsUrls.NO_IMAGE_URL)
            else
                context.loadImage(v.iv_fav_image, AppConstantsUrls.getPosterPath(posterpath))
            v.tv_fav_title.text = movie.title
            v.tv_fav_realease_date.text = movie.release_date
            v.tv_vote_count.text = movie.vote_count.toString()
            v.fav_ratingBar.rating = (movie.vote_average.toFloat()) / 2
            v.tv_popularity.text = movie.popularity.toString()
        }

        override fun onLongClick(p0: View?): Boolean {
            itemTouchHelper.startDrag(this)
            return false
        }
    }


}