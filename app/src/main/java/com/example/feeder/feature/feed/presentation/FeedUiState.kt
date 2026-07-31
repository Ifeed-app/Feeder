package com.example.feeder.feature.feed.presentation

import com.example.feeder.proto.content.v1.FeedVideo

data class FeedUiState (
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val videos: List<FeedVideo> = emptyList(),
    val errorMessage: String? = null,
    val hasMoreVideos: Boolean = true
)