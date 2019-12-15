package com.example.movies.models

import android.os.Parcel
import android.os.Parcelable
import com.example.movies.common.interfaces.BaseItem
import com.example.movies.common.utils.ViewTypeConstants
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

data class PopularModel(
    var adult: Boolean,
    var backdropPath: String?,
    var genreIds: ArrayList<Any>?,
    var id: Int,
    var originalLanguage: String?,
    var originalTitle: String?,
    var overview: String?,
    var popularity: Double?,
    var poster_path: String?,
    var release_date: String?,
    var title: String?,
    var video: Boolean,
    var vote_average: Double,
    var vote_count: Int

):Parcelable, BaseItem{


    constructor(parcel: Parcel) : this(
        parcel.readBoolean(),
        parcel.readString(),
        parcel.readArrayList(ClassLoader.getSystemClassLoader()),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readBoolean(),
        parcel.readDouble(),
        parcel.readInt()

    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (adult) 1 else 0)
        parcel.writeString(backdropPath)
        parcel.writeList(genreIds as List<*>?)
        parcel.writeInt(id)
        parcel.writeString(originalLanguage)
        parcel.writeString(originalTitle)
        parcel.writeString(overview)
        popularity?.let { parcel.writeDouble(it) }
        parcel.writeString(poster_path)
        parcel.writeString(release_date)
        parcel.writeString(title)
        parcel.writeByte(if (video) 1 else 0)
        parcel.writeDouble(vote_average)
        parcel.writeInt(vote_count)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PopularModel> {
        override fun createFromParcel(parcel: Parcel): PopularModel {
            return PopularModel(parcel)
        }

        override fun newArray(size: Int): Array<PopularModel?> {
            return arrayOfNulls(size)
        }
    }

    override fun getItemType(): Int {
        return ViewTypeConstants.POPULAR_MODEL
    }

}