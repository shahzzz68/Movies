package com.example.movies.common.base.recyclerView

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

 abstract class BaseRecyclerViewHolder(val view: View) : RecyclerView.ViewHolder(view),
    View.OnClickListener,
    View.OnLongClickListener,
    View.OnTouchListener,
    GestureDetector.OnGestureListener {
    val gestureDetector = GestureDetector(view.context,this)
    constructor(view: View, isClickable: Boolean) : this(view) {

        //view.setOnTouchListener(this)
        if (isClickable)
            view.setOnClickListener(this)
        view.setOnLongClickListener(this)
    }

    override fun onClick(p0: View?) {

    }

    override fun onLongClick(p0: View?): Boolean {
        return false
    }

    protected abstract fun populateView(): BaseRecyclerViewHolder

    override fun onShowPress(p0: MotionEvent?) {
        
    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        return false
    }

    override fun onDown(p0: MotionEvent?): Boolean {
         return false
    }

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
         return false
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
     return false
    }

    override fun onLongPress(p0: MotionEvent?) {

    }

     override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
         gestureDetector.onTouchEvent(p1)
         return true
     }
 }