package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

open class Chapter() : Parcelable {

    @SerializedName("id")
    var id = 0

    @SerializedName("title")
    lateinit var title: String

    @SerializedName("type")
    var type: String = ""

    @SerializedName("created_at")
    var createdAt = 0L

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        title = parcel.readString()!!
        type = parcel.readString()!!
        createdAt = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(type)
        parcel.writeLong(createdAt)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Chapter> {
        override fun createFromParcel(parcel: Parcel): Chapter {
            return Chapter(parcel)
        }

        override fun newArray(size: Int): Array<Chapter?> {
            return arrayOfNulls(size)
        }
    }
}