package com.example.juntavecinos.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AdminRetrofitClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8082/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val adminApi: AdminApi = retrofit.create(AdminApi::class.java)
}
