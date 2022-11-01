package com.online.coursestore.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class VideoCipherData() : Parcelable{

    @SerializedName("otp")
    var otp: String? = null
    @SerializedName("playbackInfo")
    var playbackInfo: String? = null

    constructor(parcel: Parcel) : this() {
        otp = parcel.readString()
        playbackInfo = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(otp)
        parcel.writeString(playbackInfo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VideoCipherData> {
        override fun createFromParcel(parcel: Parcel): VideoCipherData {
            return VideoCipherData(parcel)
        }

        override fun newArray(size: Int): Array<VideoCipherData?> {
            return arrayOfNulls(size)
        }
    }
}