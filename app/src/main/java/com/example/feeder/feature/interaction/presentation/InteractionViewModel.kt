package com.example.feeder.feature.interaction.presentation


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feeder.feature.interaction.data.InteractionRemoteDataSource
import com.example.feeder.proto.interaction.v1.GetVideoInteractionSummaryResponse
import com.example.feeder.proto.interaction.v1.InteractionType
import io.grpc.Status
import io.grpc.StatusException
import io.grpc.StatusRuntimeException
import java.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
data class VideoInteractionState(
    val videoId: String = "",
    val likeCount: Long = 0,
    val isLiked: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class InteractionViewModel(
    private val interactionRemoteDataSource: InteractionRemoteDataSource,
    private val userIdProvider: () -> String?
) : ViewModel() {

    private val _interactionStates =
        MutableStateFlow<Map<String, VideoInteractionState>>(
            emptyMap()
        )

    val interactionStates:
            StateFlow<Map<String, VideoInteractionState>> =
        _interactionStates.asStateFlow()

    fun getStateForVideo(
        videoId: String
    ): VideoInteractionState {
        return _interactionStates.value[videoId]
            ?: VideoInteractionState(videoId = videoId)
    }

    fun loadInteractionSummary(
        videoId: String
    ) {
        if (videoId.isBlank()) return

        val currentState = getStateForVideo(videoId)

        if (currentState.isLoading) return

        updateVideoState(
            videoId = videoId
        ) {
            it.copy(
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                val userId = requireUserId()

                val response =
                    interactionRemoteDataSource
                        .getVideoInteractionSummary(
                            userId = userId,
                            videoId = videoId
                        )

                applySummary(
                    videoId = videoId,
                    response = response
                )
            } catch (exception: Exception) {
                updateVideoState(
                    videoId = videoId
                ) {
                    it.copy(
                        isLoading = false,
                        errorMessage =
                            mapInteractionError(exception)
                    )
                }
            }
        }
    }

    fun toggleLike(
        videoId: String
    ) {
        if (videoId.isBlank()) return

        val currentState = getStateForVideo(videoId)

        if (currentState.isLoading) return

        val wasLiked = currentState.isLiked
        val previousLikeCount = currentState.likeCount

        val optimisticLikeCount = if (wasLiked) {
            (previousLikeCount - 1).coerceAtLeast(0)
        } else {
            previousLikeCount + 1
        }

        updateVideoState(
            videoId = videoId
        ) {
            it.copy(
                isLiked = !wasLiked,
                likeCount = optimisticLikeCount,
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                val userId = requireUserId()

                interactionRemoteDataSource.createInteraction(
                    userId = userId,
                    videoId = videoId,
                    interactionType = if (wasLiked) {
                        InteractionType.INTERACTION_TYPE_UNLIKE
                    } else {
                        InteractionType.INTERACTION_TYPE_LIKE
                    }
                )

                updateVideoState(
                    videoId = videoId
                ) {
                    it.copy(
                        isLoading = false
                    )
                }
            } catch (exception: Exception) {
                updateVideoState(
                    videoId = videoId
                ) {
                    it.copy(
                        isLiked = wasLiked,
                        likeCount = previousLikeCount,
                        isLoading = false,
                        errorMessage =
                            mapInteractionError(exception)
                    )
                }
            }
        }
    }

    fun recordSkip(
        videoId: String
    ) {
        if (videoId.isBlank()) return

        viewModelScope.launch {
            try {
                val userId = requireUserId()

                interactionRemoteDataSource.skipVideo(
                    userId = userId,
                    videoId = videoId
                )
            } catch (exception: Exception) {
                updateVideoState(
                    videoId = videoId
                ) {
                    it.copy(
                        errorMessage =
                            mapInteractionError(exception)
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun recordWatchSession(
        videoId: String,
        watchedSeconds: Int,
        completed: Boolean,
        startedAt: Instant,
        endedAt: Instant = Instant.now()
    ) {
        if (videoId.isBlank()) return
        if (watchedSeconds < 0) return
        if (endedAt.isBefore(startedAt)) return

        viewModelScope.launch {
            try {
                val userId = requireUserId()

                interactionRemoteDataSource
                    .recordWatchSession(
                        userId = userId,
                        videoId = videoId,
                        watchedSeconds = watchedSeconds,
                        completed = completed,
                        startedAt = startedAt,
                        endedAt = endedAt
                    )
            } catch (exception: Exception) {
                updateVideoState(
                    videoId = videoId
                ) {
                    it.copy(
                        errorMessage =
                            mapInteractionError(exception)
                    )
                }
            }
        }
    }

    fun clearError(
        videoId: String
    ) {
        updateVideoState(
            videoId = videoId
        ) {
            it.copy(
                errorMessage = null
            )
        }
    }

    private fun applySummary(
        videoId: String,
        response: GetVideoInteractionSummaryResponse
    ) {
        updateVideoState(
            videoId = videoId
        ) {
            it.copy(
                likeCount = response.likeCount,
                isLiked = response.likedByUser,
                isLoading = false,
                errorMessage = null
            )
        }
    }

    private fun requireUserId(): String {
        return userIdProvider()
            ?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException(
                "User session is not available"
            )
    }

    private fun updateVideoState(
        videoId: String,
        update: (
            VideoInteractionState
        ) -> VideoInteractionState
    ) {
        val currentMap = _interactionStates.value
        val currentState = currentMap[videoId]
            ?: VideoInteractionState(
                videoId = videoId
            )

        _interactionStates.value =
            currentMap + (
                    videoId to update(currentState)
                    )
    }

    private fun mapInteractionError(
        exception: Exception
    ): String {
        val status = when (exception) {
            is StatusException ->
                exception.status

            is StatusRuntimeException ->
                exception.status

            else -> null
        }

        return when (status?.code) {
            Status.Code.UNAUTHENTICATED ->
                "Your session has expired. Please sign in again."

            Status.Code.PERMISSION_DENIED ->
                status.description
                    ?: "You are not allowed to perform this action."

            Status.Code.INVALID_ARGUMENT ->
                status.description
                    ?: "Invalid interaction data."

            Status.Code.UNAVAILABLE ->
                "Interaction service is currently unavailable."

            else ->
                status?.description
                    ?: exception.message
                    ?: "Interaction failed."
        }
    }
}