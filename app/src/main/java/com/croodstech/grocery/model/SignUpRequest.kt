package com.croodstech.grocery.model

class SignUpRequest(
    private val companyId: String,
    private val customerEmail: String,
    private val customerFirstName: String,
    private val customerLastName: String,
    private val customerMobile: String
)
