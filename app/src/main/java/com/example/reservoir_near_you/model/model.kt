package com.example.reservoir_near_you.model

import android.text.format.DateFormat

data class Magasinstatistikk(
    val dato_Id: String,
    val omrType: String,
    val omrnr: Int,
    val iso_aar: Int,
    val iso_uke: Int,
    val fyllingsgrad: Float,
    val kapasitet_TWh: Float,
    val fylling_TWh: Float,
    val neste_Publiseringsdato: DateFormat,
    val fyllingsgrad_forrige_uke: Float,
    val endring_fyllingsgrad: Float
)