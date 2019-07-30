package com.croodstech.grocery.model

class OrderListResponse (
     var status: Boolean? = false,
    var message: String? = "",
    var response: List<OrderVo>? = null
)
