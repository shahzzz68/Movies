package com.example.movies.network

import com.example.movies.common.interfaces.BaseItem
import com.example.movies.common.utils.NetworkUtils

class HttpResponceItem() {

    var responseCode: Int = 0
    var response: String? = null
    var items: ArrayList<BaseItem>? = null
    var httpRequestUrl: String? = null
    var httpRequestEndPoint: String? = null
    var httpRequestType = NetworkUtils.HTTP_GET
    var isInBackGround = false


    constructor(responceCode:Int, responce:String):this(){

        this.responseCode=responceCode
        this.response=responce
    }

    fun getDefaultResponse(): String {
        return "Rest API $httpRequestUrl response is $response($responseCode)"
    }
}