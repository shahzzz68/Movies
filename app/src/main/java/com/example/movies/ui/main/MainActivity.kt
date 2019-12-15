package com.example.movies.ui.main

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.movies.R
import com.example.movies.common.base.recyclerView.BaseRecyclerViewActivity
import com.example.movies.common.base.recyclerView.BaseRecyclerViewHolder
import com.example.movies.common.interfaces.BaseItem
import com.example.movies.common.utils.*
import com.example.movies.models.MoviesModel
import com.example.movies.models.PopularModel
import com.example.movies.models.Progress
import com.example.movies.network.HttpRequestItem
import com.example.movies.network.HttpResponceItem
import com.example.movies.network.MyAsyncTask
import com.example.movies.ui.adapters.MoviesAdapter
import com.example.movies.ui.adapters.NowPlayingAdapter
import com.example.movies.ui.adapters.ViewPagerAdapter
import com.example.movies.ui.main.DetailsActivity.DetailActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : BaseRecyclerViewActivity(), View.OnClickListener {

    private var currentPage: Int = 0
    lateinit var viewPagerAdapter: ViewPagerAdapter
    lateinit var timer: Timer
    var res: String? = null
    var mainAdapter: MoviesAdapter? = null
    var nowPlayingAdapter: NowPlayingAdapter? = null
    var fetchPopularUrl: String? = null
    var fetchUpcommingUrl = AppConstantsUrls.getServerUrl(AppConstantsUrls.GET_UPCOMMING)
    var fetchNowPlayingUrl = AppConstantsUrls.getServerUrl(AppConstantsUrls.GET_NOW_PLAYING)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main)
        setActionBar("Home", false)

        /// fun clicklisteners
        clickListeners()

        fetchViewpagerdata()
        fetchMovies(fetchUpcommingUrl)
        fetchMovies(fetchNowPlayingUrl)

    }


    private fun clickListeners() {
        layout_search.setOnClickListener(this)
        tv_viewAll.setOnClickListener(this)
        tv_np_viewAll.setOnClickListener(this)
    }


    override fun onResume() {
        super.onResume()
        MoviesModel.isGrid = false
    }


    fun fetchViewpagerdata() {
        fetchPopularUrl = AppConstantsUrls.getServerUrl(AppConstantsUrls.GET_POULAR)
        val httpRequestItem =
            HttpRequestItem(fetchPopularUrl!!)
        httpRequestItem.setParams(httpRequestItem.apiParms())
        MyAsyncTask(getProgressDialog(), this).execute(httpRequestItem)

    }

    fun fetchMovies(fetchUpcommingUrl: String) {

        val httpRequestItem =
            HttpRequestItem(fetchUpcommingUrl)
        httpRequestItem.setParams(httpRequestItem.apiParms())
        MyAsyncTask(getProgressDialog(), this).execute(httpRequestItem)
    }


    fun vpTimer() {
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                currentPage = position
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        val handler = Handler()

        val update = Runnable {
            if (currentPage == viewPagerAdapter.getCount()) {
                currentPage = 0
            }
            viewPager.setCurrentItem(currentPage++, true)
        }

        if (::timer.isInitialized) {
            timer.cancel()
        }

        timer = Timer()
        timer.schedule(object : TimerTask() {

            override fun run() {
                handler.post(update)
            }
        }, 1000, 2500)
    }

    fun populateVpData(jsonObject: JSONObject) {
        val results =
            if (jsonObject.has("results")) jsonObject.getJSONArray("results") else JSONArray()

        val popularItems = Gson().fromJson<List<PopularModel>>(results.toString())

        viewPagerAdapter =
            ViewPagerAdapter(supportFragmentManager, popularItems)
        viewPager.adapter = viewPagerAdapter
        indicator.setupWithViewPager(viewPager, true)
        vpTimer()

    }


    private fun addProgressItem() {
        val items = ArrayList<BaseItem>()
        items.add(Progress())
        populateRvData(items)

    }


    private fun removeProgressItem() {
        if (mainAdapter!!.getAdapterCount() > 0 && mainAdapter!!.getItemAt(mainAdapter!!.getAdapterCount() - 1) is Progress
        ) {
            mainAdapter?.apply {
                remove(this.getAdapterCount() - 1)
                notifyItemRemoved(this.getAdapterCount() - 1)
            }

        }
//        if (getSwipeLayout() != null) {
//            getSwipeLayout().setRefreshing(false)
//        }
    }

    fun retriveMovies(json: JSONObject, type: String) {
        val moviesResults =
            if (json.has("results")) json.getJSONArray("results") else JSONArray()
//        val upcommingItems =
//            Gson().fromJson<MutableList<BaseItem>>(upcommingResults.toString(), object :
//                TypeToken<List<MoviesModel>>() {}.type)//Gson().fromJson<MutableList<BaseItem>>(upcommingResults.toString())

        val moviesItemsList =
            Gson().fromJson<List<MoviesModel>>(moviesResults.toString())
        when (type) {
            fetchUpcommingUrl -> populateRvData(moviesItemsList)
            fetchNowPlayingUrl -> populateRvNowPlayingData(moviesItemsList)
        }

    }

    fun populateRvData(list: MutableList<BaseItem>) {

        mainAdapter = MoviesAdapter(this, list, this@MainActivity)
        recyclerView.adapterAndManager(mainAdapter!!, true)
    }

    fun populateRvNowPlayingData(list: MutableList<BaseItem>) {

        nowPlayingAdapter = NowPlayingAdapter(this, list, this@MainActivity)
        rv_now_playing.adapterAndManager(nowPlayingAdapter!!, true)
    }

    fun deleteSession() {
        val httpRequestItem =
            HttpRequestItem(AppConstantsUrls.DELETE_SESSION)
        val postParams = mutableMapOf<String, Any>()

        postParams.apply {
            SharedPreferenceManager.getInstance(this@MainActivity)!!.read(
                SharedPreferenceManager.SESSION_ID,
                "no value"
            )
                ?.let {
                    put(
                        "session_id",
                        it
                    )
                }
        }
        httpRequestItem.apply {
            setHttpRequestType(NetworkUtils.HTTP_DELETE)
            setParams(postParams)
        }
        MyAsyncTask(getProgressDialog(), this).execute(httpRequestItem)
    }

    override fun onNetworkSuccess(response: HttpResponceItem) {
        super.onNetworkSuccess(response)
        try {

            val jsonObject = JSONObject(response.response!!)
            when (response.httpRequestUrl) {
                fetchPopularUrl -> populateVpData(jsonObject)
                fetchUpcommingUrl -> retriveMovies(jsonObject, fetchUpcommingUrl)
                fetchNowPlayingUrl -> retriveMovies(jsonObject, fetchNowPlayingUrl)
                AppConstantsUrls.DELETE_SESSION -> {
                    if (jsonObject.has("status_message")) {
                        view().showSnackBar(jsonObject.getString("status_message"))
                    } else {
                        view().showSnackBar("Session Deleted")
                        SharedPreferenceManager.getInstance(this).clearPreferences()
                    }
                }
            }

        } catch (exception: JSONException) {

            Logger.error(false, exception)
        }

    }

    override fun onNetworkError(response: HttpResponceItem) {
        showToast(response.response!!)
    }

    override fun onRecyclerViewChildItemClick(
        holder: BaseRecyclerViewHolder,
        resourceId: Int,
        imgview: ImageView
    ) {
        lateinit var moviesModel: MoviesModel

        when (holder) {
            is MoviesAdapter.UpcommingViewHolder ->
                moviesModel = mainAdapter!!.getItemAt(holder.adapterPosition) as MoviesModel
            is NowPlayingAdapter.NowPlayingViewHolder ->
                moviesModel = nowPlayingAdapter!!.getItemAt(holder.adapterPosition) as MoviesModel
        }

        val activityOptions = ActivityOptions.makeSceneTransitionAnimation(this, imgview, "anim")
        startActivity(Intent(this, DetailActivity::class.java).apply {
            putExtra("title", moviesModel.title)
            putExtra("poster_path", moviesModel.poster_path ?: AppConstantsUrls.NO_IMAGE_URL)
            putExtra("overview", moviesModel.overview)
            putExtra("rating", moviesModel.vote_average)
            putExtra("votes", moviesModel.vote_count)
            putExtra("date", moviesModel.release_date)
            putExtra("movieId", moviesModel.id)
        }, activityOptions.toBundle())
        ViewUtils.showToast(this, moviesModel.title)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.session_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.account_session -> startNewActivity<LoginActivity>()
            R.id.delete_session -> deleteSession()
            R.id.fav_movies -> startNewActivity<FavouriteMovieActivity>()
            R.id.watch_list -> startNewActivity<WatchListActivity>()

        }
        return true
    }

    override fun onClick(p0: View?) {
        val id = p0?.id
        when (id) {
            R.id.layout_search -> startNewActivity<SearchActivity>()
            R.id.tv_viewAll -> startActivityWithExtra<UpcommingMovieActivity>(
                AppConstantsKeys.KEY_MOVIE_URL,
                AppConstantsUrls.getServerUrl(AppConstantsUrls.GET_UPCOMMING)
            )
            R.id.tv_np_viewAll -> startActivityWithExtra<UpcommingMovieActivity>(
                AppConstantsKeys.KEY_MOVIE_URL,
                AppConstantsUrls.getServerUrl(AppConstantsUrls.GET_NOW_PLAYING)
            )
        }
    }
}
