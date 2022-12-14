package com.online.coursestore.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.online.coursestore.R
import java.util.*

class ChapterTextItem() : Parcelable, CourseCommonItem {

    @SerializedName("id")
    var id = 0

    @SerializedName("title")
    lateinit var title: String

    @SerializedName("image")
    var image: String? = null

    @SerializedName("study_time")
    var studyTime = 0

    @SerializedName("summary")
    lateinit var summary: String

    @SerializedName("content")
    lateinit var content: String

    @SerializedName("status")
    lateinit var status: String

    @SerializedName("accessibility")
    lateinit var accessibility: String

    @SerializedName("locale")
    var locale: String = Locale.ENGLISH.language

    @SerializedName("attachments")
    var attachments: List<ChapterFileItem> = emptyList()

    @SerializedName("created_at")
    var createdAt = 0L

    @SerializedName("auth_has_read")
    var authHasRead = false

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        title = parcel.readString()!!
        image = parcel.readString()
        studyTime = parcel.readInt()
        summary = parcel.readString()!!
        content = parcel.readString()!!
        status = parcel.readString()!!
        accessibility = parcel.readString()!!
        createdAt = parcel.readLong()
        attachments = parcel.createTypedArrayList(ChapterFileItem)!!
        authHasRead = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeInt(studyTime)
        parcel.writeString(summary)
        parcel.writeString(content)
        parcel.writeString(status)
        parcel.writeString(accessibility)
        parcel.writeLong(createdAt)
        parcel.writeTypedList(attachments)
        parcel.writeByte(if (authHasRead) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChapterTextItem> {
        override fun createFromParcel(parcel: Parcel): ChapterTextItem {
            return ChapterTextItem(parcel)
        }

        override fun newArray(size: Int): Array<ChapterTextItem?> {
            return arrayOfNulls(size)
        }
    }

    override fun title(context: Context): String {
        return title
    }

    override fun desc(context: Context): String {
        return summary
    }

    override fun imgResource(context: Context): Int {
        return R.drawable.ic_paper
    }

    override fun imgBgResource(context: Context): Int {
        return R.drawable.round_view_orange_corner10
    }

    override fun passed(): Boolean {
        return authHasRead
    }
}