package com.example.feeder.feature.interaction.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.feeder.feature.interaction.data.InteractionRemoteDataSource

class InteractionViewModelFactory(
    private val interactionRemoteDataSource:
    InteractionRemoteDataSource,
    private val userIdProvider: () -> String?
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                InteractionViewModel::class.java
            )
        ) {
            return InteractionViewModel(
                interactionRemoteDataSource =
                    interactionRemoteDataSource,
                userIdProvider = userIdProvider
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}