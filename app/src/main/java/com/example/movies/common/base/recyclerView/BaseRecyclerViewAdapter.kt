package com.example.movies.common.base.recyclerView

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.util.size
import androidx.recyclerview.widget.RecyclerView
import com.example.movies.R
import com.example.movies.common.interfaces.BaseItem
import com.example.movies.common.interfaces.ItemTouchInterface
import com.example.movies.common.interfaces.RecyclerViewClickListener
import com.example.movies.common.utils.ViewTypeConstants
import java.util.*


abstract class BaseRecyclerViewAdapter(
    open var items: MutableList<BaseItem>?,
    open var recyclerViewClickListener: RecyclerViewClickListener

) : RecyclerView.Adapter<BaseRecyclerViewHolder>(), ItemTouchInterface {

    val DURATION: Long = 500
    private var onAttach = true

    var selectedItems: SparseBooleanArray = SparseBooleanArray()
    private var current_selected_idx = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder {
        val view: View
        var holder: BaseRecyclerViewHolder? = null
        if (viewType == ViewTypeConstants.ITEM_PROGRESS) {
            view = LayoutInflater.from(parent.context)
                .inflate(com.example.movies.R.layout.row_progress, parent, false)
            holder = ProgressViewHolder(view)
        } else {
            return createSpecificViewHolder(parent, viewType)
        }

        return holder
    }

    override fun onBindViewHolder(holder: BaseRecyclerViewHolder, position: Int) {
        if (holder !is ProgressViewHolder) {
            bindSpecificViewHolder(holder, position)
        }
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    fun getItemAt(position: Int): BaseItem? {
        return items?.get(position)
    }

    fun clearItems() {
        if (items != null) {
            val size = items!!.size
            if (size > 0) {
                for (i in 0 until size)
                    items!!.removeAt(0)
                notifyItemRangeRemoved(0, size)
            }
        }
    }


    abstract fun createSpecificViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseRecyclerViewHolder

    abstract fun bindSpecificViewHolder(
        holder: BaseRecyclerViewHolder,
        position: Int
    )


    private inner class ProgressViewHolder(val v: View) :
        BaseRecyclerViewHolder(v) {

        protected override fun populateView(): BaseRecyclerViewHolder {
            return this
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItemAt(position)!!.getItemType()
    }

    fun getAdapterCount(): Int {
        return items?.size ?: 0
    }

    fun addAll(newItems: MutableList<BaseItem>) {
        if (items == null)
            items = ArrayList()

        newItems.forEach {
            items!!.add(it)
            notifyItemInserted(items!!.size - 1)
        }

//        for (item in newItems) {
//
//            items!!.add(item)
//            notifyItemInserted(items!!.size - 1)
//        }
    }

    fun clearResources() {
        items = null

    }

    fun remove(position: Int) {
        items?.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items!!.size)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                onAttach = false
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        super.onAttachedToRecyclerView(recyclerView)
    }

    open fun setAnimation(itemView: View, i: Int) {
        var j = i
        if (!onAttach) {
            j = -1
        }
        val isNotFirstItem = j == -1
        j++
        itemView.alpha = 0f
        val animatorSet = AnimatorSet()
        val animator = ObjectAnimator.ofFloat(itemView, "alpha", 0f, 0.5f, 1.0f)
        ObjectAnimator.ofFloat(itemView, "alpha", 0f).start()
        animator.startDelay = if (isNotFirstItem) DURATION / 2
        else j * DURATION / 3
        animator.duration = 500
        animatorSet.play(animator)
        animator.start()
    }


    /////////////////////////  multi Handling selection////////////////////

    fun toggleSelection(pos: Int) {
        current_selected_idx = pos
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos)
        } else {
            selectedItems.put(pos, true)
        }
        notifyItemChanged(pos)
    }

    fun selectedItemsList(): MutableList<Int> {
        val selectedList = mutableListOf<Int>()
        for (i in 0 until selectedItems.size)
            selectedList.add(selectedItems.keyAt(i))
        return selectedList
    }

    fun clearSelections() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    private fun resetCurrentIndex() {
        current_selected_idx = -1
    }

    fun toggleCheck(pos: Int, imageView: ImageView) {
        if (selectedItems.get(pos, false)) {
            imageView.setImageResource(R.drawable.default_image)
            if (current_selected_idx == pos) resetCurrentIndex()
        }
    }

    fun selectedItemCount(): Int {
        return selectedItems.size()
    }
    ///////////////////////////////////////////////////////////
/////////////////////  TOUCH LISTENER///////////////////////////
    override fun onItemMove(fromPosition: Int, toPosition: Int) {

    }

    override fun onItemSwiped(position: Int) {
    }


}