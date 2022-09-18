package com.online.course.model

import com.google.gson.annotations.SerializedName

class AddToCart {

    @SerializedName("webinar_id")
    var webinarId = 0

    @SerializedName("ticket_id")
    var pricingPlanId : Int? = null
}