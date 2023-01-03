package com.online.coursestore.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Checkout () : Parcelable {

    @SerializedName("discount_id")
    var discount_id: String? = null

    @SerializedName("webinar_id")
    var webinar_id: String? = null

    @SerializedName("PaymentMethodId")
    var PaymentMethodId: String? = null

    @SerializedName("gateway_id")
    var gateway_id: String? = null

    @SerializedName("return")
    var return_type: String? = null

    constructor(parcel: Parcel) : this() {
        discount_id = parcel.readString()
        webinar_id = parcel.readString()
        PaymentMethodId = parcel.readString()
        gateway_id = parcel.readString()
        return_type = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(discount_id)
        parcel.writeString(webinar_id)
        parcel.writeString(PaymentMethodId)
        parcel.writeString(gateway_id)
        parcel.writeString(return_type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Checkout> {
        override fun createFromParcel(parcel: Parcel): Checkout {
            return Checkout(parcel)
        }

        override fun newArray(size: Int): Array<Checkout?> {
            return arrayOfNulls(size)
        }
    }
}