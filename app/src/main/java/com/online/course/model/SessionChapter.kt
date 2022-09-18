package com.online.course.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.online.course.R
import com.online.course.manager.Utils

class SessionChapter : Chapter, Parcelable {

    @SerializedName("sessions")
    var sessions: List<ChapterSessionItem> = emptyList()

    constructor()

    constructor(parcel: Parcel) : super(parcel) {
        sessions = parcel.createTypedArrayList(ChapterSessionItem)!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeTypedList(sessions)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SessionChapter> {
        override fun createFromParcel(parcel: Parcel): SessionChapter {
            return SessionChapter(parcel)
        }

        override fun newArray(size: Int): Array<SessionChapter?> {
            return arrayOfNulls(size)
        }
    }
}