package com.example.reservoir_near_you.model

data class MagasinX(
    val dato_Id: String,
    val endring_fyllingsgrad: Float,
    val fylling_TWh: Double,
    val fyllingsgrad: Float,
    val fyllingsgrad_forrige_uke: Double,
    val id: Int,
    val iso_uke: Int,
    val kapasitet_TWh: Double,
    val latitude: Double,
    val longitude: Double,
    val name: String
)