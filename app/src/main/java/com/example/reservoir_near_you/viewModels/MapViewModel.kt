package com.example.reservoir_near_you.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.google.android.gms.maps.model.LatLng

class MapViewModel: ViewModel() {

    private var _locationHashMap = MutableLiveData<HashMap<LatLng, String>>()
    val locationHashMap: LiveData<HashMap<LatLng, String>>
        get() = _locationHashMap

    fun setLocationHashMap(latLng: LatLng, string: String){
        /*var localHashMap = HashMap<LatLng, String>()
        localHashMap[latLng] = string*/
        _locationHashMap.value?.put(latLng, string)
        locationHashMap.value
    }

    fun getLocationHashMapData():LiveData<HashMap<LatLng, String>>{
        return locationHashMap
    }
}