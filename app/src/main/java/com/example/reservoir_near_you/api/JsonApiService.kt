package com.example.reservoir_near_you.api

import com.example.reservoir_near_you.model.Magasinstatistikk
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "http://api.nve.no/doc/magasinstatistikk/"

interface SimpleApi {
    @GET("/HentOffentligData")
    suspend fun getMagasin(
        @Query("dato_id") dato_Id: String
    ): Response<Magasinstatistikk>
}

object RetrofitInstance {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val retrofit by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val api: SimpleApi by lazy {
        retrofit.create(SimpleApi::class.java)
    }
}