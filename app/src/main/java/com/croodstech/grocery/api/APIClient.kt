package com.croodstech.grocery.api

import retrofit2.Retrofit

object APIClient {
    private var retrofit: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit? {
        val interceptor = okhttp3.logging.HttpLoggingInterceptor()


        interceptor.level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
        val client = okhttp3.OkHttpClient.Builder()
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor(interceptor).build()

        if (retrofit == null) {
            retrofit = retrofit2.Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .client(client)
                .build()
        }
        return retrofit
    }
}
