package com.example.reservoir_near_you.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reservoir_near_you.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response
import com.example.reservoir_near_you.model.Magasin
import com.example.reservoir_near_you.model.MagasinX

class MagasinViewModel(private val repository: Repository) : ViewModel() {

    val magasinRespone: MutableLiveData<Response<Magasin>> = MutableLiveData()
    val allMagasinResponse: MutableLiveData<Response<Magasin>> = MutableLiveData()

    fun getMagasin(id: Int){
        viewModelScope.launch {
            val response = repository.getMagasin(id)
            magasinRespone.value = response
        }
    }

    fun getAllMagasin(){
        viewModelScope.launch {
            val response = repository.getAllMagasin()
            allMagasinResponse.value = response

        }
    }
}