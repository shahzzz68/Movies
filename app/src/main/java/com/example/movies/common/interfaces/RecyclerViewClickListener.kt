package com.example.movies.common.interfaces

import android.widget.ImageView
import com.example.movies.common.base.recyclerView.BaseRecyclerViewHolder

interface RecyclerViewClickListener {

     fun onRecyclerViewItemClick(holder: BaseRecyclerViewHolder)

     fun onRecyclerViewChildItemClick(holder: BaseRecyclerViewHolder, resourceId: Int ,imgview:ImageView)

     fun onRecyclerViewLongClick(holder: BaseRecyclerViewHolder, resourceId: Int)
}