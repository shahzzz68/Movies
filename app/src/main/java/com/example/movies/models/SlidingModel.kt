package com.example.movies.models

import android.os.Parcel
import android.os.Parcelable
import com.example.movies.common.interfaces.BaseItem
import com.example.movies.common.utils.ViewTypeConstants

class SlidingModel(
    var imageId:Int,
    var title: String?
):Parcelable ,BaseItem{
    override fun getItemType(): Int {
        return ViewTypeConstants.SLIDE_MODEL
    }

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(imageId)
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SlidingModel> {
        override fun createFromParcel(parcel: Parcel): SlidingModel {
            return SlidingModel(parcel)

        }


        override fun newArray(size: Int): Array<SlidingModel?> {
            return arrayOfNulls(size)
        }
    }
}