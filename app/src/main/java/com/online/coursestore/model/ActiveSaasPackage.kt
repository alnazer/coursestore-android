package com.online.coursestore.model

import com.google.gson.annotations.SerializedName

class ActiveSaasPackage {

    @SerializedName("title")
    var title = ""

    @SerializedName("activation_date")
    var activationDate = 0L

    @SerializedName("days_remained")
    var daysRemained = ""
}