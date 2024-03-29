package com.example.reservoir_near_you.api

import com.example.reservoir_near_you.model.Magasin
import com.example.reservoir_near_you.model.MagasinX
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "http://85.159.209.56:5000/"

interface SimpleApi {

    @GET("/magasin")
    suspend fun getMagasin(
        @Query("name") name: String
    ): Response<Magasin>

    @GET("/magasin")
    suspend fun getAllMagasin(): Response<Magasin>
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