package com.example.movies.common.utils

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movies.common.base.recyclerView.BaseRecyclerViewAdapter
import com.example.movies.common.interfaces.BaseItem
import com.example.movies.models.Progress
import com.example.movies.network.HttpRequestItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.HashMap


fun HttpRequestItem.apiParms(): HashMap<String, Any> {

    val mutableMap = HashMap<String, Any>()
    mutableMap.put("api_key", AppConstantsUrls.API_KEY)
    mutableMap.put("page", random(1, 5))
    return mutableMap
}

fun HttpRequestItem.random(from: Int, to: Int): Int {
    val random = Random()
    return random.nextInt(to - from) + from
}

///  using gson for type token
inline fun <reified T> Gson.fromJson(json: String): MutableList<BaseItem> =
    this.fromJson<MutableList<BaseItem>>(json, object : TypeToken<T>() {}.type)

////   start new activity
inline fun <reified A : Activity> Activity.startNewActivity() {
    this.startActivity(Intent(this, A::class.java))
}

inline fun <reified A : Activity> Activity.startActivityWithExtra(key: String, value: String) {
    this.startActivity(Intent(this, A::class.java).apply {
        putExtra(key, value)
    })
}

/// recycler view///

fun RecyclerView.adapterAndManager(
    adapter: BaseRecyclerViewAdapter?,
    isLinearHorizontal: Boolean = false,
    isGrid: Boolean = false,
    spanSize: Int = 2
) {
    this.adapter = adapter
    this.layoutManager =
        if (isGrid) {
            GridLayoutManager(context, spanSize).apply {
                orientation = LinearLayoutManager.VERTICAL
                setSpanSizeLookup(object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (adapter?.getItemAt(position) is Progress) spanSize else 1
                    }
                })
            }

        } else {

            if (isLinearHorizontal)
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            else
                LinearLayoutManager(context)
        }
}


/// hide soft keyboard

fun Activity.hideSoftKeyboard() {

    val inputMethodManager = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    val view = this.currentFocus
    view?.run {
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }
}


fun View.onclick(click: () -> Unit) =
    this.setOnClickListener {
        click()
    }


//fun View.click(activity: Activity) =
//    this.setOnClickListener(activity as View.OnClickListener)

