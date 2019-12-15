package com.example.movies.ui.main

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.movies.R
import com.example.movies.arch_components.room.entities.Movie
import com.example.movies.common.base.recyclerView.BaseRecyclerViewActivity
import com.example.movies.common.base.recyclerView.BaseRecyclerViewHolder
import com.example.movies.common.interfaces.BaseItem
import com.example.movies.common.utils.*
import com.example.movies.common.utils.AppConstantsKeys
import com.example.movies.models.Progress
import com.example.movies.models.MoviesModel
import com.example.movies.network.HttpRequestItem
import com.example.movies.network.HttpResponceItem
import com.example.movies.network.MyAsyncTask
import com.example.movies.ui.adapters.MoviesAdapter
import com.example.movies.ui.main.DetailsActivity.DetailActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_search.recyclerView
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class UpcommingMovieActivity : BaseRecyclerViewActivity() {


    private var pageNo = 1
    var mainAdapter: MoviesAdapter? = null
    lateinit var handler: Handler
    var fetchAnyUrl = AppConstantsUrls.getServerUrl(AppConstantsUrls.GET_UPCOMMING)
    private var doLoadMore = false
    var selectedPos: Int = -1
    var searchText = ""
    var itemList: MutableList<BaseItem>? = null
    var upcommingItems: MutableList<BaseItem>? = null
    var isToastShow: Boolean = false
    var actionMode: androidx.appcompat.view.ActionMode? = null
    lateinit var actionModeCallback: ActionModeCallback


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upcomming_movie)


        getExtras()

        actionModeCallback = ActionModeCallback()

        recyclerViewScroll()
        fetchUpcommingMovies(true)
    }

    fun getExtras()
    {
        val extras= intent.extras?.getString(AppConstantsKeys.KEY_MOVIE_URL,"")
        fetchAnyUrl = extras!!

        when(extras)
        {
            AppConstantsUrls.getServerUrl(AppConstantsUrls.GET_UPCOMMING)->
                setActionBar("Upomming Movies", true)
            AppConstantsUrls.getServerUrl(AppConstantsUrls.GET_NOW_PLAYING)->
                setActionBar("Now Playing", true)
        }

    }


    fun fetchUpcommingMovies(progress: Boolean) {

        if (progress)
            addProgressItem()

        MoviesModel.isGrid = true
        val httpRequestItem =
            HttpRequestItem(fetchAnyUrl)
        val mutableMap = HashMap<String, Any>()
        mutableMap.put("api_key", AppConstantsUrls.API_KEY)
        mutableMap.put("page", pageNo)
        httpRequestItem.setParams(mutableMap)
        MyAsyncTask(null, this).execute(httpRequestItem)
    }


    fun retriveUpcomming(json: JSONObject) {

        val upcommingResults =
            if (json.has("results")) json.getJSONArray("results") else JSONArray()
        upcommingItems = Gson().fromJson<List<MoviesModel>>(upcommingResults.toString())
        // mutableList.addAll(upcommingItems)
        // populateRvData(mutableList)
        setItemsResponse(upcommingItems!!)
    }

    @Throws(JSONException::class)
    private fun setItemsResponse(data: MutableList<BaseItem>) {
        itemList = mutableListOf<BaseItem>()

        if (data.size == 0) {
            doLoadMore = false
            showToast("No Data Found")
            return
        } else {
            if (data.size < 20) {
                isToastShow = true
            }
            pageNo++
            doLoadMore = data.size == 20
            itemList!!.addAll(data)
        }

        populateRvData(itemList)
    }

    fun populateRvData(list: MutableList<BaseItem>?) {

        removeProgressItem()
        MoviesModel.isGrid = true

        mainAdapter?.addAl(list!!) ?: run {

            mainAdapter = MoviesAdapter(this, list, this@UpcommingMovieActivity)
            recyclerView.adapterAndManager(mainAdapter, isGrid = true, spanSize = 2)

        }

    }

    private fun addProgressItem() {
        val items = mutableListOf<BaseItem>()
        items.add(Progress())
        populateRvData(items)
    }

    private fun removeProgressItem() {
        if (mainAdapter != null && mainAdapter?.getAdapterCount()!! > 0 &&
            mainAdapter?.getItemAt(mainAdapter?.getAdapterCount()!! - 1) is Progress
        ) {

            mainAdapter?.apply {
                remove(mainAdapter!!.getAdapterCount() - 1)
                notifyItemRemoved(mainAdapter!!.getAdapterCount() - 1)
            }

            // moviesAdapter = null

        }
    }

    override fun onNetworkSuccess(response: HttpResponceItem) {
        super.onNetworkSuccess(response)
        removeProgressItem()

        try {
            Logger.debug("Progess dialog", "removed")
            val jsonObject = JSONObject(response.response!!)
            when (response.httpRequestUrl) {
                fetchAnyUrl -> retriveUpcomming(jsonObject)
            }


        } catch (exception: JSONException) {

            Logger.error(false, exception)
        }
    }

    fun recyclerViewScroll() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)


                val reachBottom = !recyclerView.canScrollVertically(1)
                if (reachBottom) {

                    if (isToastShow)
                        showToast("No More data")

                    if (!doLoadMore)
                        return

                    fetchUpcommingMovies(true)
                    doLoadMore = false
                }
            }
        })

    }

    override fun onRecyclerViewChildItemClick(
        holder: BaseRecyclerViewHolder,
        resourceId: Int,
        imgview: ImageView
    ) {
        if (mainAdapter!!.selectedItems.size() > 0) {
            enableActionMode(holder.adapterPosition)
        } else {
            MoviesModel.isGrid = false
            val moviesModel: MoviesModel? =
                mainAdapter?.getItemAt(holder.adapterPosition) as MoviesModel

            val activityOptions =
                ActivityOptions.makeSceneTransitionAnimation(this, imgview, "anim")
            startActivity(Intent(this, DetailActivity::class.java).apply {
                putExtra("title", moviesModel!!.title)
                putExtra("poster_path", moviesModel.poster_path ?: AppConstantsUrls.NO_IMAGE_URL)
                putExtra("overview", moviesModel.overview)
                putExtra("rating", moviesModel.vote_average)
                putExtra("votes", moviesModel.vote_count)
                putExtra("date", moviesModel.release_date)
                putExtra("movieId", moviesModel.id)
            }, activityOptions.toBundle())
        }

    }

    override fun onRecyclerViewLongClick(holder: BaseRecyclerViewHolder, resourceId: Int) {

        enableActionMode(holder.adapterPosition)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    ///////////////////////////////   HANDLING SELECTION///////////////////////////

    private fun enableActionMode(position: Int) {

        actionMode ?: run {
            actionMode =
                startSupportActionMode(object : androidx.appcompat.view.ActionMode.Callback {
                    override fun onActionItemClicked(
                        mode: androidx.appcompat.view.ActionMode?,
                        item: MenuItem?
                    ): Boolean {
                        when (item?.itemId) {
                            R.id.action_mode -> {
                                showToast(mainAdapter!!.selectedItemsList().toString())
                                return true
                            }
                        }
                        return false
                    }

                    override fun onCreateActionMode(
                        mode: androidx.appcompat.view.ActionMode?,
                        menu: Menu?
                    ): Boolean {
                        toolbar.visibility = View.GONE
                        mode?.menuInflater?.inflate(R.menu.action_mode_menu, menu)
                        return true
                    }

                    override fun onPrepareActionMode(
                        mode: androidx.appcompat.view.ActionMode?,
                        menu: Menu?
                    ): Boolean {
                        return false
                    }

                    override fun onDestroyActionMode(mode: androidx.appcompat.view.ActionMode?) {
                        toolbar.visibility = View.VISIBLE
                        actionMode = null
                        mainAdapter!!.clearSelections()

                    }
                })

        }

        toggleSelection(position)
    }

    private fun toggleSelection(position: Int) {
        mainAdapter!!.toggleSelection(position)
        val count = mainAdapter!!.selectedItemCount()
        if (count == 0) {
            actionMode!!.finish()
        } else {
            actionMode!!.title = count.toString()
            actionMode!!.invalidate()
        }
    }

    inner class ActionModeCallback : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode?, menuItem: MenuItem?): Boolean {
            when (menuItem?.itemId) {
                R.id.action_mode -> {
                    mainAdapter!!.selectedItemsList(); return true
                }
            }
            return false
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.menuInflater?.inflate(R.menu.action_mode_menu, menu)
            return true
        }

        override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
            return false
        }

        override fun onDestroyActionMode(p0: ActionMode?) {
            actionMode = null
            mainAdapter!!.clearSelections()
        }

    }
}

