package com.example.movies.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.example.movies.R
import com.example.movies.common.base.recyclerView.BaseRecyclerViewActivity
import com.example.movies.common.base.recyclerView.BaseRecyclerViewHolder
import com.example.movies.common.utils.*
import com.example.movies.models.PopularModel
import com.example.movies.network.HttpRequestItem
import com.example.movies.network.HttpResponceItem
import com.example.movies.network.MyAsyncTask
import com.example.movies.ui.adapters.FavMoviesAdapter
import com.example.movies.ui.main.DetailsActivity.DetailActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_favourite_movie.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONObject

class FavouriteMovieActivity : BaseRecyclerViewActivity() {

    var sessionId: String? = null
    var favMoviesAdapter: FavMoviesAdapter? = null
    var actionMode: androidx.appcompat.view.ActionMode? = null
    val listForDelete = mutableListOf<PopularModel>()
    var isDeleteCalled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite_movie)
        setActionBar("Favourite Movies", true)

        sessionId =
            SharedPreferenceManager.getInstance(this)?.read(SharedPreferenceManager.SESSION_ID, "")

        fetchFavMovies()
    }

    fun fetchFavMovies() {
        val httpRequestItem =
            HttpRequestItem(AppConstantsUrls.FAV_MOVIES_URL)
        val querryParams = mutableMapOf<String, Any>().apply {
            put("api_key", AppConstantsUrls.API_KEY)
            put("session_id", sessionId!!)
            put("sort_by", "created_at.desc")
        }
        httpRequestItem.setParams(querryParams)
        MyAsyncTask(getProgressDialog(), this).execute(httpRequestItem)
    }


    fun populateData(jsonObject: JSONObject) {
        val results =
            if (jsonObject.has("results")) jsonObject.getJSONArray("results") else JSONArray()

        val favMoviesList = Gson().fromJson<List<PopularModel>>(results.toString())
        favMoviesAdapter = FavMoviesAdapter(this, favMoviesList, this)
        rv_fav_movies.adapterAndManager(favMoviesAdapter)

    }

    fun deleteFavMovies(movieId: Int, isFasle: Boolean) {
        val httpRequestItem =
            HttpRequestItem(AppConstantsUrls.markFavUrl(sessionId!!))
        val postParams = mutableMapOf<String, Any>().apply {
            put("media_type", "movie")
            put("media_id", movieId)
            put("favorite", isFasle)
        }
        httpRequestItem.apply {
            setHttpRequestType(NetworkUtils.HTTP_POST)
            setParams(postParams)
        }
        MyAsyncTask(getProgressDialog(), this).execute(httpRequestItem)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////     NETWORK CALLBACKS///////////////////////////////////////////////
    override fun onNetworkSuccess(response: HttpResponceItem) {
        val jsonObject = JSONObject(response.response!!)

        when (response.httpRequestUrl) {
            AppConstantsUrls.FAV_MOVIES_URL -> populateData(jsonObject)

            AppConstantsUrls.markFavUrl(sessionId!!) -> {
                if (isDeleteCalled) {
//                    favMoviesAdapter!!.items!!.removeAll(listForDelete)
//                    favMoviesAdapter!!.notifyDataSetChanged()
                    //favMoviesAdapter!!.notifyItemRangeRemoved(0, favMoviesAdapter!!.itemCount-1)

                    for (i in listForDelete.size -1 downTo 0 ) {
                        favMoviesAdapter!!.remove(
                            favMoviesAdapter!!.selectedItems.keyAt(
                                i
                            )
                        )
                    }
                    favMoviesAdapter!!.selectedItems.clear()
                    listForDelete.clear()
                    isDeleteCalled = false
                }
            }
        }
    }


    override fun onNetworkError(response: HttpResponceItem) {
        showToast(response.response!!)
    }

    /////////////////////  RECYCLER VIEW CLICK LISTENERS//////////////////////////////////
    ///////////////////////////////////////////////////////////////
    override fun onRecyclerViewChildItemClick(
        holder: BaseRecyclerViewHolder,
        resourceId: Int,
        imgview: ImageView
    ) {
        val popularModel: PopularModel =
            favMoviesAdapter!!.getItemAt(holder.adapterPosition) as PopularModel

        if (favMoviesAdapter!!.selectedItemCount() > 0) {
            enableActionMode(holder.adapterPosition)
        } else {
            startActivity(Intent(this, DetailActivity::class.java).apply {
                putExtra("title", popularModel.title)
                putExtra("poster_path", popularModel.poster_path ?: AppConstantsUrls.NO_IMAGE_URL)
                putExtra("overview", popularModel.overview)
                putExtra("rating", popularModel.vote_average)
                putExtra("votes", popularModel.vote_count)
                putExtra("date", popularModel.release_date)
                putExtra("movieId", popularModel.id)
            })
        }
    }

    override fun onRecyclerViewLongClick(holder: BaseRecyclerViewHolder, resourceId: Int) {
        enableActionMode(holder.adapterPosition)
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

                                for (i in 0 until favMoviesAdapter!!.selectedItemCount()) {

                                    isDeleteCalled = true
                                    val popularModel: PopularModel =
                                        favMoviesAdapter!!.getItemAt(
                                            favMoviesAdapter!!.selectedItems.keyAt(
                                                i
                                            )
                                        ) as PopularModel
                                    listForDelete.add(popularModel)
                                    deleteFavMovies(popularModel.id, false)
                                }

                                toolbar.visibility = View.VISIBLE
                                actionMode!!.finish()
                                actionMode = null
                                favMoviesAdapter!!.clearSelections()

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
                        favMoviesAdapter!!.clearSelections()

                    }
                })

        }

        toggleSelection(position)
    }

    private fun toggleSelection(position: Int) {
        favMoviesAdapter!!.toggleSelection(position)
        val count = favMoviesAdapter!!.selectedItemCount()
        if (count == 0) {
            actionMode!!.finish()
        } else {
            actionMode!!.title = count.toString()
            actionMode!!.invalidate()
        }
    }
}
