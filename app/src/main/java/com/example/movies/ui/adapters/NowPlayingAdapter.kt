package com.example.movies.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.movies.R
import com.example.movies.common.base.recyclerView.BaseRecyclerViewAdapter
import com.example.movies.common.base.recyclerView.BaseRecyclerViewHolder
import com.example.movies.common.interfaces.BaseItem
import com.example.movies.common.interfaces.RecyclerViewClickListener
import com.example.movies.common.utils.AppConstantsUrls
import com.example.movies.common.utils.loadImage
import com.example.movies.models.MoviesModel
import kotlinx.android.synthetic.main.row_upcomming_movies.view.*

class NowPlayingAdapter(
    var context: Context,
    override var items: MutableList<BaseItem>?,
    override var recyclerViewClickListener: RecyclerViewClickListener
) : BaseRecyclerViewAdapter(items,recyclerViewClickListener) {

    override fun createSpecificViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseRecyclerViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_upcomming_movies, parent, false)
        return NowPlayingViewHolder(view)
    }

    override fun bindSpecificViewHolder(holder: BaseRecyclerViewHolder, position: Int) {

        if (holder is NowPlayingViewHolder)
        {
            val nowPlayingViewHolder : NowPlayingViewHolder = holder
            val movies: MoviesModel = getItemAt(position) as MoviesModel

            nowPlayingViewHolder.apply {

                txtv.text = movies.title
                context.loadImage(
                    imgV, AppConstantsUrls.getPosterPath(
                        movies.poster_path
                    )
                )
            }
        }
    }


    inner class NowPlayingViewHolder(v:View) : BaseRecyclerViewHolder(v,true) {

        var txtv = v.tv_upcomming
        var imgV = v.imgvUpcomming

        init {
//             txtv = v.findViewById(R.movieId.tv_upcomming)
//             imgV = v.findViewById(R.movieId.imgvUpcomming)
            v.setOnClickListener {
                recyclerViewClickListener.onRecyclerViewChildItemClick(this, it.id, imgV)
            }
        }

        override fun populateView(): BaseRecyclerViewHolder {
            return this
        }
    }
}