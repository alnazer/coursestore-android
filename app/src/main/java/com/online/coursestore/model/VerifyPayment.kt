package com.online.coursestore.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class VerifyPayment() : Parcelable {

    @SerializedName("order_id")
    var orderId: Int = 0

    @SerializedName("status")
    var status: String? = null

    constructor(parcel: Parcel) : this() {
        orderId = parcel.readInt()
        status = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(orderId)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VerifyPayment> {
        override fun createFromParcel(parcel: Parcel): VerifyPayment {
            return VerifyPayment(parcel)
        }

        override fun newArray(size: Int): Array<VerifyPayment?> {
            return arrayOfNulls(size)
        }
    }

}