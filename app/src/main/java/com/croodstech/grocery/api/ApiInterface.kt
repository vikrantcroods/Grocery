package com.croodstech.grocery.api

import com.croodstech.grocery.model.*
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.POST



interface ApiInterface {

    @FormUrlEncoded
    @POST("category")
    fun getHomeDeliveryList(@Field("companyId") companyId : String,@Header("Authorization") auth : String) : Call<HomeDeliveryResponse>

    @FormUrlEncoded
    @POST("subcategory")
    fun getSubCategoryList(@Field("companyId") companyId : String,@Field("categoryId") categoryId : Int,@Header("Authorization") auth : String) : Call<HomeDeliveryResponse>

    @FormUrlEncoded
    @POST("product")
    fun getProductList(@Field("companyId") companyId : String,@Field("categoryId") categoryId : Int,@Header("Authorization") auth : String) : Call<ProductListModel>



    @FormUrlEncoded
    @POST("auth/checkMobNo")
    fun checkMobileNo(@Field("customerMobile") customerMobile : String,@Field("companyId") companyId : String) : Call<CheckMobileNoResponse>

    @POST("auth/signup")
    fun signUp(@Body request : SignUpRequest) : Call<CommonResponse>

    @FormUrlEncoded
    @POST("auth/signin")
    fun signIn(@Field("customerMobile") customerMobile : String,@Field("companyId") companyId : String) : Call<CheckMobileNoResponse>

    @FormUrlEncoded
    @POST("auth/verifyotp")
    fun verifyOtp(@Field("customerMobile") customerMobile : String,@Field("otp") otp : String,@Field("companyId") companyId : String) : Call<CommonResponse>




    @FormUrlEncoded
    @POST("addcart")
    fun addToCart(@Field("companyId") companyId : String,@Field("productVarientId") productVarientId : String,@Field("type") type : String,@Header("Authorization") auth : String) : Call<CommonResponse>

    @FormUrlEncoded
    @POST("viewcart")
    fun viewCartList(@Field("companyId") companyId : String,@Header("Authorization") auth : String) : Call<CartListModel>

    @FormUrlEncoded
    @POST("placeorder")
    fun placeOrder(@Field("companyId") companyId : String,@Header("Authorization") auth : String) : Call<CheckMobileNoResponse>

    @FormUrlEncoded
    @POST("lastorder")
    fun orderList(@Field("companyId") companyId : String,@Header("Authorization") auth : String) : Call<OrderListResponse>

    @FormUrlEncoded
    @POST("deletecart")
    fun deleteCartItem(@Field("cartId") cartId : String, @Field("companyId") companyId : String, @Header("Authorization") auth : String) : Call<CheckMobileNoResponse>




    @FormUrlEncoded
    @POST("address")
    fun getAllAddress(@Field("companyId") companyId : String,@Header("Authorization") auth : String) : Call<AddressModel>

    @POST("city")
    fun getAllCity(@Header("Authorization") auth : String) : Call<CityResponse>

    @Headers("Accept: application/json")
    @POST("addaddress")
    fun addAddress(@Body request:  AddAddressRequest,@Header("Authorization") auth : String): Call<CheckMobileNoResponse>

    @FormUrlEncoded
    @POST("defaultaddress")
    fun setDefaultAddress(@Field("addressId") addressId : String, @Header("Authorization") auth : String) : Call<CheckMobileNoResponse>

}
