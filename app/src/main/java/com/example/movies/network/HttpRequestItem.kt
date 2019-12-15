package com.example.movies.network

import com.example.movies.common.utils.AppUtils
import com.example.movies.common.utils.NetworkUtils
import java.util.HashMap

class HttpRequestItem(val url:String) {


    /**
     * Name-value pair of http prams like name=john doe
     */
    private var params: MutableMap<String, Any>? = null
    /**
     * Name-value pair of http header prams like authorization: basic xxxxxxx
     */
    private var headerParams: MutableMap<String, String>? = null
    /**
     * URL of given request
     */
    private var httpRequestUrl: String? = null
    /**
     * URL endPoint of given request
     */
    private var httpRequestEndPoint = ""
    /**
     * HTTP request type either [NetworkUtils.HTTP_GET] or [NetworkUtils.HTTP_POST]
     */
    private var httpRequestType = NetworkUtils.HTTP_GET
    /**
     * Http request timeout value
     */
    private var httpRequestTimeout: Long = 0

    private var isInBackGround = false

    init {
        if (!AppUtils.ifNotNullEmpty(url))
            throw NullPointerException("Http request url can not be null")
        this.httpRequestUrl = url
    }


    fun getHttpRequestType(): String {
        return httpRequestType
    }

    fun setHttpRequestType(httpRequestType: String) {
        this.httpRequestType = httpRequestType
    }

    fun getParams(): Map<String, Any>? {
        return params
    }

    fun setParams(params: MutableMap<String, Any>) {
        this.params = params
    }

    /**
     * Add single name-value pair for http param into [Map]
     *
     * @param name  name
     * @param value value
     */
    fun addParams(name: String, value: String) {
        if (params == null)
            params = HashMap()
        params!![name] = value
    }

    fun getHttpRequestUrl(): String? {
        return httpRequestUrl
    }

    fun setHttpRequestUrl(httpRequestUrl: String) {
        this.httpRequestUrl = httpRequestUrl
    }

    fun getHttpRequestEndPoint(): String {
        return httpRequestEndPoint
    }

    fun setHttpRequestEndPoint(httpRequestEndPoint: String) {
        this.httpRequestEndPoint = httpRequestEndPoint
    }

    fun setHeaderParams(headerParams: MutableMap<String, String>) {
        this.headerParams = headerParams
    }

    /**
     * name-value pair for http header
     *
     * @param name  name
     * @param value value
     */
    fun addHeaderParams(name: String, value: String) {
        if (headerParams == null)
            headerParams = HashMap()
        headerParams!![name] = value
    }

    fun getHeaderParams(): Map<String, String>? {
        return headerParams
    }

    fun getHttpRequestTimeout(): Long {
        return httpRequestTimeout
    }

    fun setHttpRequestTimeout(httpRequestTimeout: Long) {
        this.httpRequestTimeout = httpRequestTimeout
    }

    fun isInBackGround(): Boolean {
        return isInBackGround
    }

    fun setInBackGround(inBackGround: Boolean) {
        isInBackGround = inBackGround
    }
}