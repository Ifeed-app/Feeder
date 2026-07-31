package com.example.feeder.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.ViewModelFactoryDsl
import com.example.feeder.data.remote.AuthRemoteDataSource
import com.example.feeder.feature.auth.data.AuthSessionManager

class AuthViewModelFactory (private val authRemoteDataSource: AuthRemoteDataSource, private val sessionManager: AuthSessionManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(
                authRemoteDataSource = authRemoteDataSource,
                sessionManager = sessionManager
            ) as T
        }
        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )

    }

}