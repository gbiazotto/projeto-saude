package com.fagundes.projetosaude.rede

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInitializer {

    private val url = "http://34.229.179.92:3000"

    private val retrofit =  Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    fun getAPIs(): APIs = retrofit.create(APIs::class.java)

}