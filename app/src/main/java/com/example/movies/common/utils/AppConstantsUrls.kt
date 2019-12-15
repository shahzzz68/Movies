package com.example.movies.common.utils

object AppConstantsUrls {

    val BASE_URL = "https://api.themoviedb.org/3/"
    val NO_IMAGE_URL =
        "https://uwosh.edu/facilities/wp-content/uploads/sites/105/2018/09/no-photo.png"
    val API_KEY = "992392f5e986ab8d04d5c2ffe24c3707"
    val GET_POULAR = "popular"
    val GET_UPCOMMING = "upcoming"
    val GET_NOW_PLAYING = "now_playing"
    val GET_REQ_TOKEN = "${BASE_URL}authentication/token/new?api_key=$API_KEY"
    val VALIDATE_WITH_LOGIN = "${BASE_URL}authentication/token/validate_with_login?api_key=$API_KEY"
    val GUEST_USER = "https://api.themoviedb.org/3/authentication/guest_session/new"
    val CREATE_SESSION= "${BASE_URL}authentication/session/new?api_key=$API_KEY"
    val DELETE_SESSION= "${BASE_URL}authentication/session?api_key=$API_KEY"
    val FAV_MOVIES_URL = "${BASE_URL}account/{account_id}/favorite/movies"


    fun movieDetailsUrl(movieId : Int)=
        "${BASE_URL}movie/$movieId?api_key=$API_KEY&language=en-US"

    fun markFavUrl(sessionId:String) =
        "${BASE_URL}account/{account_id}/favorite?api_key=$API_KEY&session_id=$sessionId"

    fun getServerUrl(endPoint: String) =
        "${BASE_URL}movie/$endPoint"

    fun getSearchUrl() =
        "${BASE_URL}search/movie"

    fun getRateMovieUrl(id:Int,sessionId:String)=
        "${BASE_URL}movie/$id/rating?api_key=$API_KEY&session_id=$sessionId"

    fun getPosterPath(path: String?): String {
        val posterPath: String? = path?.let { "https://image.tmdb.org/t/p/w500$it" }
        return posterPath ?: NO_IMAGE_URL
    }

}