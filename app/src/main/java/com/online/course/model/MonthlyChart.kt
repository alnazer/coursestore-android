package com.online.course.model

import com.google.gson.annotations.SerializedName

class MonthlyChart {

    @SerializedName("months")
    lateinit var months : List<String>

    @SerializedName("data")
    lateinit var data: List<Double>
}