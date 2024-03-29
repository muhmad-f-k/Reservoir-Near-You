package com.example.reservoir_near_you.repository

import com.example.reservoir_near_you.api.RetrofitInstance
import com.example.reservoir_near_you.model.Magasin
import com.example.reservoir_near_you.model.MagasinX
import retrofit2.Response

class Repository {

    suspend fun getMagasin(name: String): Response<Magasin> {
        return RetrofitInstance.api.getMagasin(name)
    }

    suspend fun getAllMagasin(): Response<Magasin> {
        return RetrofitInstance.api.getAllMagasin()
    }
}