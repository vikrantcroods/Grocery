package com.croodstech.grocery.api

object UtilApi {
    //val BASE_URL_API = "http://192.168.2.93:8080/api/ecommerce/"
  //  val  BASE_URL_API = "http://patidaarsales.vasyerp.com/api/ecommerce/"
    val  BASE_URL_API = "http://partyvilla.croodstech.com/api/ecommerce/"

    // Mendeklarasikan Interface BaseApiService
    val apiService: ApiInterface
        get() = APIClient.getClient(BASE_URL_API)!!.create(ApiInterface::class.java)
}
