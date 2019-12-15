package com.example.movies.ui.adapters

import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.size
import com.example.movies.R
import com.example.movies.common.base.recyclerView.BaseRecyclerViewAdapter
import com.example.movies.common.base.recyclerView.BaseRecyclerViewHolder
import com.example.movies.common.interfaces.BaseItem
import com.example.movies.common.interfaces.RecyclerViewClickListener
import com.example.movies.common.utils.AppConstantsUrls
import com.example.movies.common.utils.ViewTypeConstants
import com.example.movies.common.utils.loadImage
import com.example.movies.models.MoviesModel
import com.example.movies.models.Progress
import kotlinx.android.synthetic.main.row_upcomming_grid.view.*
import kotlinx.android.synthetic.main.row_upcomming_movies.view.*
import java.util.*
import kotlin.collections.ArrayList

class MoviesAdapter(
    var context: Context,
    override var items: MutableList<BaseItem>?,
    override var recyclerViewClickListener: RecyclerViewClickListener
) : BaseRecyclerViewAdapter(items, recyclerViewClickListener) {

    private var current_selected_idx = -1

    override fun createSpecificViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseRecyclerViewHolder {

        val view: View
        var holder: BaseRecyclerViewHolder? = null
        if (viewType == ViewTypeConstants.UPCOMMING_LINEAR) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_upcomming_movies, parent, false)
            holder = UpcommingViewHolder(view).populateView()
        } else if (viewType == ViewTypeConstants.UPCOMMING_GRID) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_upcomming_grid, parent, false)
            holder = UpcommingViewHolderGrid(view).populateView()
        }

        return holder!!

    }


    override fun bindSpecificViewHolder(holder: BaseRecyclerViewHolder, position: Int) {

        when (holder) {
            is UpcommingViewHolder -> {
                val upcommingViewHolder: UpcommingViewHolder = holder
                val moviesModel: MoviesModel = getItemAt(position) as MoviesModel


                upcommingViewHolder.apply {

                    txtv.text = moviesModel.title
                    context.loadImage(
                        imgV, AppConstantsUrls.getPosterPath(
                            moviesModel.poster_path
                        )
                    )

//                    GlideUtils.glideLoadImageOrPlaceHolder(
//                        context,
//                        imgV,
//                        AppConstantsUrls.getPoster_path(
//                            upcommingModel.poster_path ?: AppConstantsUrls.NO_IMAGE_URL
//                        )
//                    )
                }
            }
            ///////////////
            is UpcommingViewHolderGrid -> {
                val upcommingViewHolder: UpcommingViewHolderGrid = holder
                val moviesModel: MoviesModel = getItemAt(position) as MoviesModel

                upcommingViewHolder.apply {

                    gridText.text = moviesModel.title
                    context.loadImage(
                        gridImage, AppConstantsUrls.getPosterPath(
                            moviesModel.poster_path
                        )
                    )

                    gridItem.isActivated = selectedItems.get(position, false)


//                    gridItem.setOnClickListener {
//                        recyclerViewClickListener.onRecyclerViewChildItemClick(this, it.movieId, gridImage)
//                    }


                }

//                upcommingViewHolder.gridItem.setOnLongClickListener {
//                    recyclerViewClickListener.onRecyclerViewLongClick(upcommingViewHolder,it.movieId)
//                    return@setOnLongClickListener false
//                }
               toggleCheck(position,upcommingViewHolder.gridImage)
            }
        }
    }

    inner class UpcommingViewHolder(v: View) : BaseRecyclerViewHolder(v) {

        var txtv = v.tv_upcomming
        var imgV = v.imgvUpcomming

        init {
//             txtv = v.findViewById(R.movieId.tv_upcomming)
//             imgV = v.findViewById(R.movieId.imgvUpcomming)
            v.setOnClickListener {
                recyclerViewClickListener.onRecyclerViewChildItemClick(this, it.id, imgV)
            }
        }

        public override fun populateView(): BaseRecyclerViewHolder {
            return this
        }

    }

    inner class UpcommingViewHolderGrid(v: View) : BaseRecyclerViewHolder(v, true) {

        var gridText = v.tv_upcomming_grid
        var gridImage = v.imgvUpcommingGrid
        var gridItem = v.grid_item

        init {
//            gridText = v.findViewById(R.movieId.tv_upcomming_grid)
//            gridImage = v.findViewById(R.movieId.imgvUpcommingGrid)

        }

        public override fun populateView(): BaseRecyclerViewHolder {
            return this
        }

        override fun onClick(p0: View?) {
            recyclerViewClickListener.onRecyclerViewChildItemClick(this, p0!!.id, gridImage)
        }

        override fun onLongClick(p0: View?): Boolean {
            recyclerViewClickListener.onRecyclerViewLongClick(this, p0!!.id)
            return true
        }

    }


    override fun getItemViewType(position: Int): Int {

        if (getItemAt(position) is Progress)
            return ViewTypeConstants.ITEM_PROGRESS

        return if (MoviesModel.isGrid)
            ViewTypeConstants.UPCOMMING_GRID
        else
            ViewTypeConstants.UPCOMMING_LINEAR
    }

    fun addAl(newItems: MutableList<BaseItem>) {

        val list = ArrayList<BaseItem>()
        items ?: list.also { items = it }

        newItems.forEach{
            items!!.add(it)
            notifyItemInserted(items!!.size - 1)
        }
    }

}