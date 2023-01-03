package com.online.coursestore.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class CurrencyData() : Parcelable {

    @SerializedName("code")
    var code: String? = null

    @SerializedName("title")
    var title: String? = null

    constructor(parcel: Parcel) : this() {
        code = parcel.readString()
        title = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(code)
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CurrencyData> {
        override fun createFromParcel(parcel: Parcel): CurrencyData {
            return CurrencyData(parcel)
        }

        override fun newArray(size: Int): Array<CurrencyData?> {
            return arrayOfNulls(size)
        }
    }
}