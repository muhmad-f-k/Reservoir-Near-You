package com.example.reservoir_near_you.repository

import com.example.reservoir_near_you.api.RetrofitInstance
import com.example.reservoir_near_you.model.Magasinstatistikk
import retrofit2.Response

class Repository {

    suspend fun getMagasin(dato_Id: String): Response<Magasinstatistikk> {
        return RetrofitInstance.api.getMagasin(dato_Id)
    }
}