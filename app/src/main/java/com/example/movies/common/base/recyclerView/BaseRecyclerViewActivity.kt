package com.example.movies.common.base.recyclerView

import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.movies.common.base.BaseActivity
import com.example.movies.common.interfaces.RecyclerViewClickListener
import kotlinx.android.synthetic.main.activity_main.*

open class BaseRecyclerViewActivity : BaseActivity(), RecyclerViewClickListener {

    override fun onRecyclerViewItemClick(holder: BaseRecyclerViewHolder) {

    }

    override fun onRecyclerViewChildItemClick(
        holder: BaseRecyclerViewHolder,
        resourceId: Int,
        imgview: ImageView
    ) {
    }

    override fun onRecyclerViewLongClick(holder: BaseRecyclerViewHolder, resourceId: Int) {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        super.setContentView(layoutResID)
    }


    override fun onDestroy() {
        super.onDestroy()

        recyclerView?.let {
            val adapter: BaseRecyclerViewAdapter? = it.getAdapter() as? BaseRecyclerViewAdapter
            adapter?.clearResources()
            it.adapter = null
        }
    }

}