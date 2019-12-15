package com.example.movies.ui.main

import android.app.Dialog
import android.os.Bundle
import com.example.movies.R
import com.example.movies.common.base.BaseActivity
import com.example.movies.common.utils.AppConstantsUrls
import com.example.movies.common.utils.NetworkUtils
import com.example.movies.common.utils.SharedPreferenceManager
import com.example.movies.common.utils.showToast
import com.example.movies.network.HttpRequestItem
import com.example.movies.network.HttpResponceItem
import com.example.movies.network.MyAsyncTask
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : BaseActivity() {

    var reqToken: String? = null
    var dialog:Dialog?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dialog = getProgressDialog()

        SharedPreferenceManager.getInstance(this)!!.read(
            SharedPreferenceManager.SESSION_ID,
            "no value"
        )?.let { showToast(it) }

        btn_login.setOnClickListener {
            createReqToken()
        }
    }

    fun createReqToken() {
        dialog?.show()
        val httpRequestItem =
            HttpRequestItem(AppConstantsUrls.GET_REQ_TOKEN)
        httpRequestItem.setHttpRequestType(NetworkUtils.HTTP_GET)
        MyAsyncTask(null, this).execute(httpRequestItem)
    }

    fun signIn() {
        val httpRequestItem =
            HttpRequestItem(AppConstantsUrls.VALIDATE_WITH_LOGIN)

        val postParams = mutableMapOf<String, Any>()
        postParams.apply {
            put("username", et_email.text.toString().trim())
            put("password", et_password.text.toString().trim())
            put("request_token", reqToken!!)
        }
        httpRequestItem.apply {
            setHttpRequestType(NetworkUtils.HTTP_POST)
            setParams(postParams)
        }
        MyAsyncTask(null, this).execute(httpRequestItem)
    }

    fun createNewSession() {
        val httpRequestItem =
            HttpRequestItem(AppConstantsUrls.CREATE_SESSION)

        val postParams = mutableMapOf<String, Any>()
        postParams.apply {
            put("request_token", reqToken!!)
        }
        httpRequestItem.apply {
            setHttpRequestType(NetworkUtils.HTTP_POST)
            setParams(postParams)
        }
        MyAsyncTask(null, this).execute(httpRequestItem)
    }

    fun dismissDialog()
    {
        dialog?.let {
            if (it.isShowing)
                it.dismiss()
        }
    }

    override fun onNetworkSuccess(response: HttpResponceItem) {

        val jsonObject = JSONObject(response.response!!)
        when (response.httpRequestUrl) {
            AppConstantsUrls.GET_REQ_TOKEN -> {
                reqToken=jsonObject.getString("request_token")
                signIn()
            }

            AppConstantsUrls.VALIDATE_WITH_LOGIN -> createNewSession()

            AppConstantsUrls.CREATE_SESSION -> {
                showToast(jsonObject.getString("session_id"))
                SharedPreferenceManager.getInstance(this)!!.save(
                    SharedPreferenceManager.SESSION_ID,
                    jsonObject.getString("session_id")
                )
                dismissDialog()
                finish()
            }
        }
    }

    override fun onNetworkError(response: HttpResponceItem) {

        val a = response.response!!
        showToast(response.response!!)
        dismissDialog()
    }
}

