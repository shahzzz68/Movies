package com.example.movies.common.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.movies.arch_components.room.entities.Movie
import com.example.movies.common.interfaces.BaseItem

class RecyclerDiffUtils(var oldList: MutableList<BaseItem>, var newList: MutableList<BaseItem>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val listOld=oldList[oldItemPosition] as Movie
        val listnew=newList[newItemPosition] as Movie

        return listOld.title.equals(listnew.title)
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val listOld=oldList[oldItemPosition] as Movie
        val listnew=newList[newItemPosition] as Movie
        return listOld.movieId == listnew.movieId
    }
}