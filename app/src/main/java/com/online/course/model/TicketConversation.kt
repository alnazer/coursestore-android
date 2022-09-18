package com.online.course.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class TicketConversation() : Parcelable {

    var id = 0

    @SerializedName("message")
    lateinit var message: String

    @SerializedName("sender")
    var sender: User? = null

    @SerializedName("supporter")
    var supporter: User? = null

    @SerializedName("created_at")
    var createdAt = 0L

    @SerializedName("attach")
    var attachment: String? = null

    var fileSize: Long? = null

    constructor(parcel: Parcel) : this() {
        message = parcel.readString()!!
        sender = parcel.readParcelable(User::class.java.classLoader)
        supporter = parcel.readParcelable(User::class.java.classLoader)
        createdAt = parcel.readLong()
        attachment = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(message)
        parcel.writeParcelable(sender, flags)
        parcel.writeParcelable(supporter, flags)
        parcel.writeLong(createdAt)
        parcel.writeString(attachment)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TicketConversation> {
        override fun createFromParcel(parcel: Parcel): TicketConversation {
            return TicketConversation(parcel)
        }

        override fun newArray(size: Int): Array<TicketConversation?> {
            return arrayOfNulls(size)
        }
    }

}