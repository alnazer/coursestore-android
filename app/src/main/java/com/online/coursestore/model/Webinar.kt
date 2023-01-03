package com.online.coursestore.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Webinar() : Parcelable{

    @SerializedName("image")
    var webinarImage: String? = null

    @SerializedName("id")
    var webinarId: Int = 0

    @SerializedName("status")
    var status: String? = null

    @SerializedName("video_demo")
    var videoDemo: VideoCipherData? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("duration")
    var webinarDuration: Int = 0

    @SerializedName("teacher")
    var teacher: Teacher? = null

    @SerializedName("rate")
    var rate: Float = 0f

    constructor(parcel: Parcel) : this() {
        webinarImage = parcel.readString()
        webinarId = parcel.readInt()
        status = parcel.readString()
        videoDemo = parcel.readParcelable(VideoCipherData::class.java.classLoader)
        title = parcel.readString()
        type = parcel.readString()
        webinarDuration = parcel.readInt()
        teacher = parcel.readParcelable(Teacher::class.java.classLoader)
        rate = parcel.readFloat()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(webinarImage)
        parcel.writeInt(webinarId)
        parcel.writeString(status)
        parcel.writeParcelable(videoDemo, flags)
        parcel.writeString(title)
        parcel.writeString(type)
        parcel.writeInt(webinarDuration)
        parcel.writeParcelable(teacher, flags)
        parcel.writeFloat(rate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Webinar> {
        override fun createFromParcel(parcel: Parcel): Webinar {
            return Webinar(parcel)
        }

        override fun newArray(size: Int): Array<Webinar?> {
            return arrayOfNulls(size)
        }
    }
}