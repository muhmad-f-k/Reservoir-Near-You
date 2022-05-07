package com.example.reservoir_near_you

import com.google.android.gms.maps.model.CameraPosition

interface MyClickListener {
    fun onClick(position: CameraPosition)
}