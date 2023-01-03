package com.online.coursestore.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class CheckoutResponse() : Parcelable{

    @SerializedName("user_id")
    var user_id: Int = -1

    @SerializedName("status")
    var status: String? = null

    @SerializedName("amount")
    var amount: Float = 0f

    @SerializedName("tax")
    var tax: Int = 0

    @SerializedName("total_discount")
    var total_discount: Float = 0f

    @SerializedName("total_amount")
    var total_amount: Float = 0f

    @SerializedName("created_at")
    var created_at: Long = 0L

    @SerializedName("currency_id")
    var currency_id: Int = 0

    @SerializedName("rate")
    var rate: String? = null

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("payment_data")
    var payment_data: String? = null

    @SerializedName("payment_method")
    var payment_method: String? = null

    @SerializedName("payment_commission")
    var payment_commission: String? = null

    constructor(parcel: Parcel) : this() {
        user_id = parcel.readInt()
        status = parcel.readString()
        amount = parcel.readFloat()
        tax = parcel.readInt()
        total_discount = parcel.readFloat()
        total_amount = parcel.readFloat()
        created_at = parcel.readLong()
        currency_id = parcel.readInt()
        rate = parcel.readString()
        id = parcel.readInt()
        payment_data = parcel.readString()
        payment_method = parcel.readString()
        payment_commission = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(user_id)
        parcel.writeString(status)
        parcel.writeFloat(amount)
        parcel.writeInt(tax)
        parcel.writeFloat(total_discount)
        parcel.writeFloat(total_amount)
        parcel.writeLong(created_at)
        parcel.writeInt(currency_id)
        parcel.writeString(rate)
        parcel.writeInt(id)
        parcel.writeString(payment_data)
        parcel.writeString(payment_method)
        parcel.writeString(payment_commission)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CheckoutResponse> {
        override fun createFromParcel(parcel: Parcel): CheckoutResponse {
            return CheckoutResponse(parcel)
        }

        override fun newArray(size: Int): Array<CheckoutResponse?> {
            return arrayOfNulls(size)
        }
    }
}