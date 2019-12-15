package com.example.movies.fragments


import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

import com.example.movies.R
import com.example.movies.common.interfaces.BaseItem
import com.example.movies.common.utils.AppConstantsUrls
import com.example.movies.models.PopularModel
import kotlinx.android.synthetic.main.fragment_view_pager.view.*

/**
 * A simple [Fragment] subclass.
 */
class ViewPagerFragment : Fragment() {

    val FRAGMENT_ARGUMENT_KEY="argument_key"
    lateinit var returnView: View
    lateinit var popularModel: PopularModel

    companion object {
        fun newInstance(popularModel: PopularModel): ViewPagerFragment {

            val fragment = ViewPagerFragment().apply {
                arguments= Bundle().apply {
                    putParcelable(FRAGMENT_ARGUMENT_KEY,popularModel)
                }
            }

            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(FRAGMENT_ARGUMENT_KEY))
            {
                popularModel= it.getParcelable(FRAGMENT_ARGUMENT_KEY)!!
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        returnView= inflater.inflate(R.layout.fragment_view_pager, container, false)

        popularModel?.let {
           // returnView.slideImgView.setImageResource(R.drawable.shahzeb)
            returnView.slideTxtView.text=it.title
        }
        loadImage(returnView)

        return returnView
    }


    fun loadImage(v:View)
    {
        Glide.with(this)
            .asBitmap()
            .load(AppConstantsUrls.getPosterPath(popularModel.poster_path!!))
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val b = Bitmap.createScaledBitmap(
                        resource, resources.getDimension(R.dimen.dimen_370).toInt(),
                        resources.getDimension(R.dimen.dimen_140).toInt(), false
                    )
                    val d = BitmapDrawable(context!!.resources, b)
                    v.slideImgView.setImageDrawable(d)

                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }


}
