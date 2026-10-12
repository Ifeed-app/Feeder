package com.example.feeder.feature.feed.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feeder.data.remote.ContentRemoteDataSource
import com.example.feeder.data.remote.RecommendationRemoteDataSource
import com.example.feeder.feature.auth.data.AuthSessionManager
import io.grpc.Status
import io.grpc.StatusException
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel(
    private val contentRemoteDataSource: ContentRemoteDataSource,
    private val recommendationRemoteDataSource: RecommendationRemoteDataSource,
    private val authSessionManager: AuthSessionManager
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 10
    }

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private var currentOffset = 0

    init {
        loadFeed()
    }

    fun loadFeed() {
        if (_uiState.value.isLoading) return

        currentOffset = 0

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isLoadingMore = false,
                    errorMessage = null,
                    hasMoreVideos = true
                )
            }

            try {
                val userId = authSessionManager.getUserId()

                val videos = if (userId == null) {

                    _uiState.update {
                        it.copy(
                            debugMessage = "User not signed in random parameters"
                        )
                    }
                    val response = contentRemoteDataSource.listFeedVideos(
                        limit = PAGE_SIZE,
                        offset = 0
                    )

                    currentOffset = response.videosList.size

                    response.videosList
                } else {
                    _uiState.update {
                        it.copy(
                            debugMessage = "Personalized feed - requesting recommendations"
                        )
                    }
                    val recommendationResponse =
                        recommendationRemoteDataSource.getRecommendations(
                            userId = userId,
                            limit = PAGE_SIZE
                        )

                    val videoIds = recommendationResponse.videosList
                        .map { it.videoId }

                    _uiState.update {
                        it.copy(
                            debugMessage = "ML recommendation • ${videoIds.size} videos selected"
                        )
                    }

                    if (videoIds.isEmpty()) {
                        emptyList()
                    } else {
                        contentRemoteDataSource
                            .getVideosByIds(videoIds)
                            .videosList
                    }
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        videos = videos,
                        errorMessage = null,
                        hasMoreVideos = userId == null &&
                                videos.size == PAGE_SIZE
                    )
                }

            } catch (exception: Exception) {
                exception.printStackTrace()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = mapFeedError(exception)
                    )
                }
            }
        }
    }

    fun loadMoreVideos() {
        val currentState = _uiState.value

        if (currentState.isLoading) return
        if (currentState.isLoadingMore) return
        if (!currentState.hasMoreVideos) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingMore = true,
                    errorMessage = null
                )
            }

            try {
                val response = contentRemoteDataSource.listFeedVideos(
                    limit = PAGE_SIZE,
                    offset = currentOffset
                )

                val newVideos = response.videosList

                currentOffset += newVideos.size

                _uiState.update { state ->
                    state.copy(
                        isLoadingMore = false,
                        videos = (state.videos + newVideos)
                            .distinctBy { video -> video.id },
                        hasMoreVideos = false
                    )
                }
            } catch (exception: Exception) {
                exception.printStackTrace()

                _uiState.update {
                    it.copy(
                        isLoadingMore = false,
                        errorMessage = mapFeedError(exception)
                    )
                }
            }
        }
    }

    private fun mapFeedError(exception: Exception): String {
        val status = when (exception) {
            is StatusRuntimeException -> exception.status
            is StatusException -> exception.status
            else -> return "Failed to load the feed."
        }

        return when (status.code) {
            Status.Code.UNAVAILABLE ->
                "Unable to connect to the content service."

            Status.Code.DEADLINE_EXCEEDED ->
                "The content service took too long to respond."

            Status.Code.INTERNAL ->
                "The server could not load the feed."

            else ->
                status.description ?: "Failed to load the feed."
        }
    }
}