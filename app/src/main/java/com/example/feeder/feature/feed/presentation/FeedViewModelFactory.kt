package com.example.feeder.feature.feed.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.feeder.data.remote.ContentRemoteDataSource
import com.example.feeder.data.remote.RecommendationRemoteDataSource
import com.example.feeder.feature.auth.data.AuthSessionManager

class FeedViewModelFactory(
    private val contentRemoteDataSource: ContentRemoteDataSource,
    private val recommendationRemoteDataSource: RecommendationRemoteDataSource,
    private val authSessionManager: AuthSessionManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FeedViewModel(
                contentRemoteDataSource = contentRemoteDataSource,
                recommendationRemoteDataSource = recommendationRemoteDataSource,
                authSessionManager = authSessionManager
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}