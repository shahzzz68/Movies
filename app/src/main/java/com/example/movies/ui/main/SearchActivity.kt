package com.example.movies.ui.main

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.movies.R
import com.example.movies.common.base.recyclerView.BaseRecyclerViewActivity
import com.example.movies.common.base.recyclerView.BaseRecyclerViewHolder
import com.example.movies.common.interfaces.BaseItem
import com.example.movies.common.interfaces.FilterInterface
import com.example.movies.common.utils.*
import com.example.movies.fragments.FilterFragment
import com.example.movies.models.Progress
import com.example.movies.models.MoviesModel
import com.example.movies.network.HttpRequestItem
import com.example.movies.network.HttpResponceItem
import com.example.movies.network.MyAsyncTask
import com.example.movies.ui.adapters.MoviesAdapter
import com.example.movies.ui.main.DetailsActivity.DetailActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_search.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.math.BigInteger


class SearchActivity : BaseRecyclerViewActivity(), FilterInterface {



    private var searchUrl: String? = null
    private var pageNo = 1
    var mainAdapter: MoviesAdapter? = null
    private var doLoadMore = false
    var searchText = ""
    var itemList: MutableList<BaseItem>? = null
    var upcommingItems: MutableList<BaseItem>? = null
    var isToastShow: Boolean = false
    var filterDate:Int?= null
    var filterLang:String? = null
    lateinit var searchView :androidx.appcompat.widget.SearchView
    lateinit var chipGroup:ChipGroup


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setActionBar("Search", true)

        chipGroup= findViewById(R.id.chip_group)

        searchUrl = AppConstantsUrls.getSearchUrl()
        //fetchMovies(true)
        recyclerViewScroll()
//        tv_filters.setOnClickListener {
//            startNewActivity<FullscreenActivity>()
//        }

        tv_filters.onclick {
          FilterFragment().show(supportFragmentManager,"fragment")
        }

    }

    fun addChip(string:String )
    {
        val chip= Chip(this)
        chip.apply {
            text = string
            setTextColor(ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.colorPrimary)))
            chipBackgroundColor = ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.colorAccent))
            closeIconTint=ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.colorPrimary))
            isCloseIconEnabled = true
            isClickable = false
            isCheckable = false
        }

        chip.setOnCloseIconClickListener {
            chip_group.removeView(chip as View)
           showToast(chip.text.toString())

            when(chip.text)
            {
                filterDate.toString() -> filterDate= null
                filterLang -> filterLang = null
            }

            searchView.apply {
                isFocusable=true
                isIconified=false
            }
        }

        chip_group.apply {

            if (childCount >= 2) {
                removeAllViews()
                addView(chip as View)
            }
            else
            addView(chip as View)
        }

    }


    fun searchUpcommingMovies(progress: Boolean, search: String,progressDialog: Dialog?=null) {

        if (progress)
            addProgressItem()

        MoviesModel.isGrid = true
        val httpRequestItem =
            HttpRequestItem(searchUrl!!)
        val mutableMap = HashMap<String, Any>()
        mutableMap.put("api_key", AppConstantsUrls.API_KEY)
        mutableMap.put("query", search)
        mutableMap.put("page", pageNo)
        filterDate?.run { mutableMap.put("primary_release_year", this) }
        filterLang?.run { mutableMap.put("language",this) }
        httpRequestItem.setParams(mutableMap)
        MyAsyncTask(progressDialog, this).execute(httpRequestItem)
    }

    fun retriveUpcomming(json: JSONObject) {

        val upcommingResults =
            if (json.has("results")) json.getJSONArray("results") else JSONArray()
        upcommingItems =
            Gson().fromJson<List<MoviesModel>>(upcommingResults.toString())
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

            mainAdapter = MoviesAdapter(this, list, this@SearchActivity)
            recyclerView.adapterAndManager(mainAdapter,isGrid = true,spanSize = 2)
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
//                fetchAnyUrl -> retriveMovies(jsonObject)
                searchUrl -> retriveUpcomming(jsonObject)
            }


        } catch (exception: JSONException) {

            Logger.error(false, exception)
        }
    }

    fun recyclerViewScroll() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)


//                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
//                val totalItemCount = linearLayoutManager!!.itemCount
//                val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
//                if (totalItemCount <= lastVisibleItem + 1) {
//
////                    pageNo++
////                    fetchMovies(true)
//
//
//                    // doLoadMore = false
//                }


                val reachBottom = !recyclerView.canScrollVertically(1)
                if (reachBottom) {
//                    pageNo++
//                    fetchMovies(true)
                    if (isToastShow)
                        showToast("No More data")

                    if (!doLoadMore)
                        return

                    searchUpcommingMovies(true, searchText)
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
        MoviesModel.isGrid = false
        val moviesModel: MoviesModel? =
            mainAdapter?.getItemAt(holder.adapterPosition) as MoviesModel
//
//        if (selectedPos == -1) {
//            upcommingModel!!.selected = true
//            // moviesAdapter?.notifyItemChanged(holder.adapterPosition)
//            selectedPos = holder.adapterPosition
//            moviesAdapter!!.notifyDataSetChanged();
//            ViewUtils.showToast(this, "selected pos $selectedPos")
//
//        } else {
//
//            if (upcommingModel!!.selected) {
//                upcommingModel.selected = false
//                moviesAdapter!!.notifyItemChanged(selectedPos)
//                ViewUtils.showToast(this, "removed pos $selectedPos")
//                selectedPos = -1
//
//            } else {
//                val upcommingModel1: MoviesModel =
//                    moviesAdapter?.getItemAt(selectedPos) as MoviesModel
//                upcommingModel1.selected = false
//                // moviesAdapter!!.notifyItemChanged(selectedPos)
//                upcommingModel.selected = true
//                // moviesAdapter!!.notifyItemChanged(holder.adapterPosition)
//                selectedPos = holder.adapterPosition
//                ViewUtils.showToast(this, "new pos $selectedPos")
//            }
//        }


        val activityOptions = ActivityOptions.makeSceneTransitionAnimation(this, imgview, "anim")
        startActivity(Intent(this, DetailActivity::class.java).apply {
            putExtra("title", moviesModel!!.title)
            putExtra("poster_path", moviesModel.poster_path ?: AppConstantsUrls.NO_IMAGE_URL)
            putExtra("overview", moviesModel.overview)
            putExtra("rating", moviesModel.vote_average)
            putExtra("votes", moviesModel.vote_count)
            putExtra("date", moviesModel.release_date)
            putExtra("movieId" , moviesModel.id)
        }, activityOptions.toBundle())
//        ViewUtils.showToast(this, upcommingModel.title)
    }


    /////   For go back/////////////
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu?.findItem(R.id.search_item)
        searchView =
            MenuItemCompat.getActionView(searchItem) as androidx.appcompat.widget.SearchView


        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {



//                if (searchText.equals(p0)) {
//                    view().showSnackBar("please change the search query")
//
//                } else {
                    searchView.clearFocus()
                    mainAdapter=null
                    recyclerView.adapter = mainAdapter
                    isToastShow=false

                    p0?.let {
                        pageNo = 1
                        searchText = it
                        searchUpcommingMovies(false, it,getProgressDialog())
                    }


               // }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {

                return false
            }

        })
        return super.onCreateOptionsMenu(menu)
    }




/////////   filter data interface method////////////////////////
    override fun filterData(date: String?, lang: String?) {
        chipGroup.removeAllViews()
//        filterDate=null
//        filterLang=null
        pageNo=1
        mainAdapter=null
        recyclerView.adapter = mainAdapter

        date?.let { filterDate= it.toInt() }
        lang?.let {  filterLang= it}

        filterDate?.let { addChip(it.toString()) }
        filterLang?.let { addChip(it) }

        searchView.apply {
            isFocusable=true
            isIconified=false
        }
    }


}
