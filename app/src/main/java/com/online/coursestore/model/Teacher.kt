package com.online.coursestore.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Teacher() : Parcelable{

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("full_name")
    var full_name: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        full_name = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(full_name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Teacher> {
        override fun createFromParcel(parcel: Parcel): Teacher {
            return Teacher(parcel)
        }

        override fun newArray(size: Int): Array<Teacher?> {
            return arrayOfNulls(size)
        }
    }
}