package com.online.coursestore.model

import com.google.gson.annotations.SerializedName

class CouponValidation {

    @SerializedName("amounts")
    lateinit var amounts: Amounts

    @SerializedName("discount")
    lateinit var coupon: Coupon
}