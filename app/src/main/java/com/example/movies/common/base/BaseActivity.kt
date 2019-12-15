package com.example.movies.common.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import com.example.movies.R
import com.example.movies.common.interfaces.ItemTouchInterface
import com.example.movies.common.interfaces.OnNetworkTaskListner
import com.example.movies.network.HttpResponceItem
import kotlinx.android.synthetic.main.activity_detail.*

open class BaseActivity : AppCompatActivity(), OnNetworkTaskListner, ItemTouchInterface {
    override fun onItemMove(fromPosition: Int, toPosition: Int) {

    }

    override fun onItemSwiped(position: Int) {

    }

    override fun onNetworkResponse(response: HttpResponceItem) {

        val status = response.responseCode == 200
        if (status)
            onNetworkSuccess(response)
        else
            onNetworkError(response)
    }

    override fun onNetworkSuccess(response: HttpResponceItem) {
    }

    override fun onNetworkError(response: HttpResponceItem) {
    }

    override fun onNetworkCanceled(response: HttpResponceItem) {
    }

    override fun isNetworkConnected(): Boolean {
        return true
    }


    open fun getProgressDialog(): Dialog? {
        val dialog = Dialog(this)
        dialog.apply {
            setContentView(R.layout.app_dialog)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
        }
        return dialog
    }

    fun setActionBar(s: String, back: Boolean) {

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = s
            setDisplayHomeAsUpEnabled(back)

        }
    }


}