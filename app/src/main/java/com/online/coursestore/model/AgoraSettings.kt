package com.online.coursestore.model

import com.google.gson.annotations.SerializedName

class AgoraSettings {

    @SerializedName("chat")
    var chatEnabled = false

    @SerializedName("record")
    var recordEnabled = false
}