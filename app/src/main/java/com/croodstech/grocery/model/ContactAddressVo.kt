package com.croodstech.grocery.model

class ContactAddressVo (

     var contactAddressId: Long = 0,

     var firstName: String? = "",

     var lastName: String? = "",

     var addressLine1: String? = "",

     var addressLine2: String? = "",

     var addressLine3: String? = "",

     var countriesCode: String? = "",

     var countriesName: String? = "",

     var zoneCode: String? = "",

     var stateCode: String? = "",

     var stateName: String? = "",

     var cityCode: String? = "",

     var cityName: String? = "",

     var localityCode: String? = "",

     var place: String? = "",

     var lat: Double = 0.toDouble(),

     var lng: Double = 0.toDouble(),

     var localityName: String? = "",

     var pinCode: String? = "",

     var phoneNo: String? = "",

     var isDefault: Int = 0

    )
