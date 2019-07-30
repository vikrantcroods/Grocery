package com.croodstech.grocery.model

import com.google.gson.annotations.SerializedName

class CheckMobileNoResponse
    (

    @SerializedName("status")
    var status: Boolean?,

    @SerializedName("message")
    var message: String?
)