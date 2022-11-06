package com.online.coursestore.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class SoonCourse() : Parcelable {

    @SerializedName("id")
    var id = 0

    @SerializedName("title")
    var title: String? = null

    @SerializedName("image")
    var img: String? = null

    @SerializedName("instructor")
    var instructor: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        title = parcel.readString()!!
        img = parcel.readString()
        instructor = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(img)
        parcel.writeString(instructor)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SoonCourse> {
        override fun createFromParcel(parcel: Parcel): SoonCourse {
            return SoonCourse(parcel)
        }

        override fun newArray(size: Int): Array<SoonCourse?> {
            return arrayOfNulls(size)
        }
    }

}