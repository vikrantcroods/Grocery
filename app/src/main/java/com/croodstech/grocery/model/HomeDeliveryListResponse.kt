package com.croodstech.grocery.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HomeDeliveryListResponse (

    @SerializedName("id")
    @Expose
    val id:Int?,

    @SerializedName("email")
    @Expose
    val email:String?="",

    @SerializedName("first_name")
    @Expose
    val first_name:String?="",

    @SerializedName("last_name")
    @Expose
    val last_name:String?="",

    @SerializedName("avatar")
    @Expose
    val avatar:String?=""



)