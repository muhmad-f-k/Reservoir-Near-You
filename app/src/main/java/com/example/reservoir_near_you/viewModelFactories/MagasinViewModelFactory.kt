package com.example.reservoir_near_you.viewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reservoir_near_you.repository.Repository
import com.example.reservoir_near_you.viewModels.MagasinViewModel

class MagasinViewModelFactory(private val repository: Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MagasinViewModel(repository) as T
    }
}