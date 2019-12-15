package com.example.movies.ui.main

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.movies.R
import com.example.movies.arch_components.room.entities.Movie
import com.example.movies.arch_components.room.viewModel.MovieViewModel
import com.example.movies.common.base.recyclerView.BaseRecyclerViewActivity
import com.example.movies.common.interfaces.BaseItem
import com.example.movies.common.utils.adapterAndManager
import com.example.movies.ui.adapters.FavMoviesAdapter
import com.example.movies.ui.adapters.WatchListAdapter
import com.example.movies.ui.itemTouchCallbacks.WathListActivityTouch
import kotlinx.android.synthetic.main.activity_watch_list.*


class WatchListActivity : BaseRecyclerViewActivity() {

    lateinit var movieViewModel: MovieViewModel
    lateinit var adapter: WatchListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_list)

        setActionBar("Watch List", true)

        movieViewModel = ViewModelProviders.of(this).get(MovieViewModel::class.java)

        //movieViewModel.insertMovie()

        movieViewModel.allMovies.observe(this,
            Observer<List<Movie>> {

                val list = mutableListOf<BaseItem>()
                it.forEach {
                    list.add(it)
                }
                setAdapter(list)
            })
    }

    fun setAdapter(list: MutableList<BaseItem>)
    {
        adapter = WatchListAdapter(this, list, this)
        val touchHelper = ItemTouchHelper(WathListActivityTouch(this))
        adapter.setTouchHelper(touchHelper)
        touchHelper.attachToRecyclerView(rv_watch_list)
        rv_watch_list.adapterAndManager(adapter)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
///////////////////////////////////////////////////////////////////////////////////
    //////////////// SWIPE CALLBACKS//////////////////////////////////////////////
    override fun onItemSwiped(position: Int) {
        movieViewModel.deleteMovie(adapter.getItemAt(position) as Movie)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val item = adapter.getItemAt(fromPosition) as Movie
        adapter.items!!.remove(item)
        adapter.items!!.add(toPosition,item)
        adapter.notifyItemMoved(fromPosition,toPosition)
    }
}
