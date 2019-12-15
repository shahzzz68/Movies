package com.example.movies.common.interfaces

interface ItemTouchInterface {

    fun onItemMove(fromPosition: Int, toPosition: Int)

    fun onItemSwiped(position: Int)
}