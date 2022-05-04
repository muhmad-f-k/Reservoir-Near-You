package com.example.reservoir_near_you

import androidx.lifecycle.ViewModel
import android.content.Context
import androidx.lifecycle.map
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseUser

class LoginViewModel: ViewModel() {

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val authenticationState = FirebaseUserLiveData().map {user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }
}