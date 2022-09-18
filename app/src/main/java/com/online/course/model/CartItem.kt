package com.online.course.model

import com.google.gson.annotations.SerializedName

class CartItem {

    @SerializedName("id")
    var id = 0

    @SerializedName("webinar")
    var course: Course? = null

    @SerializedName("meeting")
    var meeting: Meeting? = null
}