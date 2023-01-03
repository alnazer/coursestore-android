package com.online.coursestore.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class VerifyPaymentDetails() : Parcelable{

    @SerializedName("id")
    var orderId: Int = 0

    @SerializedName("status")
    var status: String? = null

    @SerializedName("total_amount")
    var total_amount: String? = null

    @SerializedName("currency")
    var currency: CurrencyData? = null

    @SerializedName("webinars")
    var webinars: List<Webinar>? = null

    constructor(parcel: Parcel) : this() {
        orderId = parcel.readInt()
        status = parcel.readString()
        total_amount = parcel.readString()
        currency = parcel.readParcelable(CurrencyData::class.java.classLoader)
        webinars = parcel.createTypedArrayList(Webinar)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(orderId)
        parcel.writeString(status)
        parcel.writeString(total_amount)
        parcel.writeParcelable(currency, flags)
        parcel.writeTypedList(webinars)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VerifyPaymentDetails> {
        override fun createFromParcel(parcel: Parcel): VerifyPaymentDetails {
            return VerifyPaymentDetails(parcel)
        }

        override fun newArray(size: Int): Array<VerifyPaymentDetails?> {
            return arrayOfNulls(size)
        }
    }

}