package com.example.reservoir_near_you

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reservoir_near_you.model.Magasinstatistikk
import com.example.reservoir_near_you.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class MagasinstatistikkViewModel(private val repository: Repository): ViewModel() {

    val magasinResponse: MutableLiveData<Response<Magasinstatistikk>> = MutableLiveData()

    fun getMagasin(dato_Id: String){
        viewModelScope.launch {
            val response = repository.getMagasin(dato_Id)
            magasinResponse.value = response
        }
    }
}