package com.example.movies.ui.main.DetailsActivity

import android.content.res.ColorStateList
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.RatingBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.movies.R
import com.example.movies.arch_components.room.entities.Movie
import com.example.movies.arch_components.room.viewModel.MovieViewModel
import com.example.movies.common.base.recyclerView.BaseRecyclerViewHolder
import com.example.movies.common.interfaces.BaseItem
import com.example.movies.common.utils.*
import com.example.movies.models.MovieDetailsModel
import com.example.movies.models.MoviesModel
import com.example.movies.network.HttpRequestItem
import com.example.movies.network.HttpResponceItem
import com.example.movies.network.MyAsyncTask
import com.example.movies.ui.adapters.MoviesAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_movies_detail.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class DetailActivity : MovieDetails()
    , AppBarLayout.OnOffsetChangedListener
    , View.OnClickListener {


    lateinit var fetchUpcommingUrl: String
    lateinit var moviesAdapter: MoviesAdapter
    var mIsTheTitleVisible = false
    val PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f
    val ALPHA_ANIMATIONS_DURATION = 200
    var movieId: Int? = null
    var sessionId: String? = null
    var posterPath:String? = null
    lateinit var chipGroup: ChipGroup
    var movieDetails: MovieDetailsModel? = null
    lateinit var movieViewModel: MovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullscreenReq()
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        movieViewModel = ViewModelProviders.of(this).get(MovieViewModel::class.java)

        chipGroup = findViewById(R.id.geners_chip_group)
        MoviesModel.isGrid = false
        getIntentExtras()
        fetchUpcommingMovies()
        fetchMovieDetails(movieId!!)

        sessionId =
            SharedPreferenceManager.getInstance(this).read(SharedPreferenceManager.SESSION_ID, "")

        listeners()
        observer()

    }

    fun observer()
    {
        movieViewModel.isAdded(movieId!!)
            .observe(this, Observer<Boolean> {
                tv_add_watchlist.visibility = if (it) View.GONE else View.VISIBLE
            })

    }

    fun getIntentExtras() {
        intent.extras?.let {

            movieId = it.getInt("movieId")
            posterPath = it.getString("poster_path")

            if (posterPath.equals(AppConstantsUrls.NO_IMAGE_URL)) {
                this.loadImage(uc_img_detail, AppConstantsUrls.NO_IMAGE_URL)
                this.loadImage(iv_toolbar, AppConstantsUrls.NO_IMAGE_URL)
            } else {
                this.loadImage(
                    uc_img_detail, AppConstantsUrls.getPosterPath(
                        posterPath
                    )
                )
                this.loadImage(
                    iv_toolbar, AppConstantsUrls.getPosterPath(
                        posterPath
                    )
                )

            }

            supportActionBar?.apply {
                title = it.getString("title")
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
            }

            tv_totalVotes.text = it.getInt("votes").toString()
            tv_overview.text = it.getString("overview")
            ratingBar.rating = (it.getDouble("rating").toFloat()) / 2
            tv_releaseDate.text = it.getString("date") ?: "no date found"

        }
    }



    fun fullscreenReq() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

//        window.apply {
//            setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
//                WindowManager.LayoutParams.FLAG_DIM_BEHIND)
//
//            statusBarColor= Color.TRANSPARENT
//
//        }
    }

    fun visibilityListener() {
        window.decorView.setOnSystemUiVisibilityChangeListener {
            if (it and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                Handler().postDelayed({ hideSystemUI() }, 3000)
            }

        }

    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    fun listeners() {

        tv_rate_movie.setOnClickListener(this)
        app_bar.addOnOffsetChangedListener(this)
        fab_mark_fav.setOnClickListener(this)
        tv_add_watchlist.setOnClickListener(this)
    }

    fun retriveUpcomming(json: JSONObject) {
        val upcommingResults =
            if (json.has("results")) json.getJSONArray("results") else JSONArray()
        val upcommingItems =
            Gson().fromJson<List<MoviesModel>>(upcommingResults.toString())
        populateRvData(upcommingItems)
    }

    fun populateRvData(list: MutableList<BaseItem>) {

        moviesAdapter = MoviesAdapter(this, list, this@DetailActivity)
        recyclerView.adapterAndManager(moviesAdapter, true)
    }


    fun showRatingBarDialog() {

        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        //builder.setTitle("With RatingBar")
        val dialogLayout =
            inflater.inflate(R.layout.dialog_rating_bar, null)
        val ratingDialog = dialogLayout.findViewById<RatingBar>(R.id.rb_dialog)

        builder.setView(dialogLayout)
        builder.setPositiveButton(
            "OK"
        ) { _, _ ->
            if (ratingDialog.rating in 0.5..10.0) {
                val httpRequestItem =
                    HttpRequestItem(AppConstantsUrls.getRateMovieUrl(movieId!!, sessionId!!))
                val postParams = mutableMapOf<String, Any>()
                postParams.apply {
                    put("value", (ratingDialog.rating * 2))
                }
                httpRequestItem.apply {
                    setHttpRequestType(NetworkUtils.HTTP_POST)
                    setParams(postParams)
                }

                MyAsyncTask(null, this).execute(httpRequestItem)
            } else {
                view().showSnackBar("Please add rating above 0.5", Snackbar.LENGTH_LONG)
            }

        }

        builder.create().apply {
            setOnShowListener {
                getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorAccent))
            }
            window?.setBackgroundDrawableResource(R.color.colorPrimary);
            show()
        }


    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////   NETWORK REQUESTS////////////////////////////////////////////////////////

    fun fetchUpcommingMovies() {


        fetchUpcommingUrl = AppConstantsUrls.getServerUrl(AppConstantsUrls.GET_UPCOMMING)
        val httpRequestItem =
            HttpRequestItem(fetchUpcommingUrl)
        httpRequestItem.setParams(httpRequestItem.apiParms())
        MyAsyncTask(getProgressDialog(), this).execute(httpRequestItem)
    }

    fun markAsfav() {
        val httpRequestItem =
            HttpRequestItem(AppConstantsUrls.markFavUrl(sessionId!!))
        val postParams = mutableMapOf<String, Any>().apply {
            put("media_type", "movie")
            put("media_id", movieId!!)
            put("favorite", "true")
        }
        httpRequestItem.apply {
            setHttpRequestType(NetworkUtils.HTTP_POST)
            setParams(postParams)
        }
        MyAsyncTask(getProgressDialog(), this).execute(httpRequestItem)
    }

    fun addToWatchList() {
        with(movieDetails)
        {
            movieViewModel.insertMovie(
                Movie(
                    this!!.id,
                    this.title,
                    poster_path,
                    this.release_date ?: "no date found",
                    popularity,
                    vote_count,
                    vote_average,
                    true
                )
            )
        }
        view().showSnackBar("Added to Watchlist")
    }

    fun populateMovieDetails(jsonObject: JSONObject) {

        val companiesList: MutableList<String> = mutableListOf()
        val productionList: MutableList<String> = mutableListOf()
        movieDetails =
            Gson().fromJson(jsonObject.toString(), MovieDetailsModel::class.java)

        tv_watch_time.text = minutesToHours(movieDetails!!.runtime)
        tv_about_status.text = movieDetails!!.status
        tv_about_tagline.text =
            if (movieDetails!!.tagline.equals("")) "not found" else movieDetails!!.tagline

//////////////////getting movie genres list
        for (i in movieDetails!!.genres.indices) {
            addChip(geners_chip_group,movieDetails!!.genres[i].name)
        }
//////////////////// getting production companies list
        for (i in movieDetails!!.production_companies!!.indices) {
            companiesList.add(movieDetails!!.production_companies!![i].name)
        }
////////////////////////// getting production countries list
        for (i in movieDetails!!.production_countries!!.indices) {
            productionList.add(movieDetails!!.production_countries!![i].name)
        }

        tv_about_companies.text = if (companiesList.size != 0)
            companiesList.toString().substring(
                1,
                companiesList.toString().length - 1
            ) else "not found"

        tv_about_production.text = if (productionList.size != 0)
            productionList.toString().substring(
                1,
                productionList.toString().length - 1
            ) else "not found"


    }




///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////// NETWORK CALLBACKS/////////////////////////////////////

    override fun onNetworkSuccess(response: HttpResponceItem) {
        super.onNetworkSuccess(response)
        try {

            val jsonObject = JSONObject(response.response!!)
            when (response.httpRequestUrl) {
                fetchUpcommingUrl -> retriveUpcomming(jsonObject)
                AppConstantsUrls.getRateMovieUrl(movieId!!, sessionId!!) ->
                    showToast("success")
                AppConstantsUrls.markFavUrl(sessionId!!) ->
                    view().showSnackBar("Marked as favourite")
                AppConstantsUrls.movieDetailsUrl(movieId!!) -> {
                    geners_chip_group?.removeAllViews()
                    populateMovieDetails(jsonObject)
                }
            }


        } catch (exception: JSONException) {

            Logger.error(false, exception)
        }

    }

    override fun onNetworkError(response: HttpResponceItem) {
        response.response?.let { showToast(it) }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////  RECYCLER VIEW CLICK ///////////////////////////////////
    override fun onRecyclerViewChildItemClick(
        holder: BaseRecyclerViewHolder,
        resourceId: Int,
        imgview: ImageView
    ) {
        val moviesModel: MoviesModel =
            moviesAdapter.getItemAt(holder.adapterPosition) as MoviesModel

        loadImage(uc_img_detail, AppConstantsUrls.getPosterPath(moviesModel.poster_path))
        loadImage(iv_toolbar, AppConstantsUrls.getPosterPath(moviesModel.poster_path))

        with(moviesModel) {
            toolbar_layout.title = this.title
            tv_totalVotes.text = this.vote_count.toString()
            tv_overview.text = this.overview
            ratingBar.rating = this.vote_average.toFloat() / 2
            tv_releaseDate.text = this.release_date ?: "no date found"
            movieId = this.id
        }

        fetchMovieDetails(movieId!!)
        observer()

    }
/////////////////////////////////////////////////////////////////////////////////////////

    override fun onClick(p0: View?) {

        when (p0?.id) {
            R.id.tv_rate_movie -> showRatingBarDialog()
            R.id.fab_mark_fav -> markAsfav()
            R.id.tv_add_watchlist -> addToWatchList()
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////  scroll handling/////////////////////////
    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {

        val maxScroll = appBarLayout?.getTotalScrollRange()
        val percentage = Math.abs(verticalOffset) / maxScroll!!.toFloat()

        handleLayout(percentage)
        handleToolbarTitleVisibility(percentage)
    }

    private fun handleLayout(percentage: Float) {
        val alphaValue = 1f - (percentage * 1.5f)
        rl_collapsing.alpha = alphaValue
    }

    private fun handleToolbarTitleVisibility(percentage: Float) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
//            val totalPercent= ((percentage-0.7f)*3.5f)
//            iv_toolbar.alpha=totalPercent

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(iv_toolbar, ALPHA_ANIMATIONS_DURATION, View.VISIBLE)
                mIsTheTitleVisible = true
            }

        } else {

            // iv_toolbar.alpha=0f
            if (mIsTheTitleVisible) {
                startAlphaAnimation(iv_toolbar, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE)
                mIsTheTitleVisible = false
            }
        }
    }


}
