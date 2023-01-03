package com.online.coursestore.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class VerifyPaymentResponse() : Parcelable{

    @SerializedName("status")
    var status: Boolean? = false

    @SerializedName("details")
    var details: VerifyPaymentDetails? = null

    constructor(parcel: Parcel) : this() {
        status = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        details = parcel.readParcelable(VerifyPaymentDetails::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(status)
        parcel.writeParcelable(details, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VerifyPaymentResponse> {
        override fun createFromParcel(parcel: Parcel): VerifyPaymentResponse {
            return VerifyPaymentResponse(parcel)
        }

        override fun newArray(size: Int): Array<VerifyPaymentResponse?> {
            return arrayOfNulls(size)
        }
    }

}