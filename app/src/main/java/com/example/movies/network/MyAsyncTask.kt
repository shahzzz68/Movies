package com.example.movies.network

import android.app.Dialog
import android.os.AsyncTask
import android.util.Log
import com.example.movies.BuildConfig
import com.example.movies.common.interfaces.OnNetworkTaskListner
import com.example.movies.common.utils.AppUtils
import com.example.movies.common.utils.Logger
import com.example.movies.common.utils.NetworkUtils
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.net.HttpURLConnection

class MyAsyncTask(dialog: Dialog?, networkTaskListner: OnNetworkTaskListner) :
    AsyncTask<HttpRequestItem, Void, HttpResponceItem>() {

    private val LOGGING_TAG = "LOGGING"

    private var dialog: Dialog? = null
    private val myHttpRequestUrl: String? = null
    private var onNetworkTaskListener: WeakReference<OnNetworkTaskListner>? = null
    private lateinit var httpRequestItem: HttpRequestItem

    init {

        this.dialog = dialog
        this.onNetworkTaskListener = WeakReference(networkTaskListner)
    }

    override fun onPreExecute() {
        super.onPreExecute()

        dialog?.show()
    }

    override fun doInBackground(vararg p0: HttpRequestItem?): HttpResponceItem? {
        httpRequestItem = p0[0]!!
        if (httpRequestItem == null)
            return getDefaultHttpResponse("http request item is null")

        // region INTERNET_CONNECTIVITY_CHECK
        val listener = getNetworkTaskListener()
        if (listener != null && !listener.isNetworkConnected()) {
            val item = HttpResponceItem(
                HttpURLConnection.HTTP_UNAVAILABLE,
                "http unavailable/not connected"
            )

            item.apply {
                httpRequestUrl=httpRequestItem.getHttpRequestUrl()
                httpRequestEndPoint=httpRequestItem.getHttpRequestEndPoint()
                isInBackGround=httpRequestItem.isInBackGround()
            }
            return item
        }
        // endregion

        // execute network request
        val networkResponse = NetworkUtils.executeNetworkRequest(httpRequestItem)
        // if we have some response from network executor service
        if (!AppUtils.ifNotNullEmpty(networkResponse))
            return getDefaultHttpResponse("network executor service returned nothing")
        try {
            val json = JSONObject(networkResponse)
            if (json.has("response_code")) {
                val code = json.getInt("response_code")
                val item = HttpResponceItem()

                item.apply {
                    responseCode=code
                    response=json.getString("data")
                    httpRequestUrl=httpRequestItem.getHttpRequestUrl()
                    httpRequestEndPoint=httpRequestItem.getHttpRequestEndPoint()
                    httpRequestType=httpRequestItem.getHttpRequestType()
                    isInBackGround=httpRequestItem.isInBackGround()
                }
                return item
            } else
                Logger.error(javaClass.simpleName, "")
        } catch (e: JSONException) {
            Logger.caughtException(e)
        }

        return getDefaultHttpResponse("Internal error")
    }

    override fun onPostExecute(response: HttpResponceItem?) {
        super.onPostExecute(response)

        dialog?.let {
            if (it.isShowing)
                it.dismiss()
        }
        dialog=null

        val listener = getNetworkTaskListener()
        if (response != null && listener != null) {
            listener.run { onNetworkResponse(response) }
        }

        clearResources()
        if (response != null /*&& BuildConfig.IS_DEBUG_ABLE*/) {
            Log.d(LOGGING_TAG, "URL: " + httpRequestItem.getHttpRequestUrl())
            Log.d(LOGGING_TAG, "CODE: " + response.responseCode)
            if (httpRequestItem.getParams() != null)
                Log.d(LOGGING_TAG, "Params: " + httpRequestItem.getParams().toString())
//            if (!httpRequestItem.getHttpRequestUrl().contains(AppConstants.GOOGLE_BASE_URL))
//                Log.d(LOGGING_TAG, "RESPONSE: " + response.response)
        }
    }

    override fun onCancelled() {
        super.onCancelled()
        val listener = getNetworkTaskListener()
        listener?.onNetworkCanceled(getDefaultHttpResponse("Operation canceled by user"))
        clearResources()
    }


    private fun clearResources() {
        onNetworkTaskListener?.clear()
        onNetworkTaskListener = null
    }

    private fun getNetworkTaskListener(): OnNetworkTaskListner? {
        return onNetworkTaskListener?.get()
    }

    private fun getDefaultHttpResponse(error: String): HttpResponceItem {
        val item = HttpResponceItem(HttpURLConnection.HTTP_INTERNAL_ERROR, error)
        item.httpRequestUrl = myHttpRequestUrl
        return item
    }

}