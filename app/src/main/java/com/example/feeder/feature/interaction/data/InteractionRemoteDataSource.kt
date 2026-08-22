package com.example.feeder.feature.interaction.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.feeder.proto.interaction.v1.CreateInteractionRequest
import com.example.feeder.proto.interaction.v1.CreateInteractionResponse
import com.example.feeder.proto.interaction.v1.GetVideoInteractionSummaryRequest
import com.example.feeder.proto.interaction.v1.GetVideoInteractionSummaryResponse
import com.example.feeder.proto.interaction.v1.InteractionServiceGrpcKt
import com.example.feeder.proto.interaction.v1.InteractionType
import com.example.feeder.proto.interaction.v1.RecordWatchSessionRequest
import com.example.feeder.proto.interaction.v1.RecordWatchSessionResponse
import io.grpc.Channel
import java.time.Instant
import kotlin.time.ExperimentalTime

class InteractionRemoteDataSource (channel: Channel) {
    private val stub =
        InteractionServiceGrpcKt.InteractionServiceCoroutineStub(
            channel
        )

    suspend fun createInteraction(
        userId: String,
        videoId: String,
        interactionType: InteractionType
    ): CreateInteractionResponse {
        require(userId.isNotBlank()) {
            "User ID cannot be blank"
        }

        require(videoId.isNotBlank()) {
            "Video ID cannot be blank"
        }

        require(
            interactionType !=
                    InteractionType.INTERACTION_TYPE_UNSPECIFIED
        ) {
            "Interaction type must be specified"
        }

        val request = CreateInteractionRequest
            .newBuilder()
            .setUserId(userId)
            .setVideoId(videoId)
            .setInteractionType(interactionType)
            .build()

        return stub.createInteraction(request)
    }

    suspend fun likeVideo(
        userId: String,
        videoId: String
    ): CreateInteractionResponse {
        return createInteraction(
            userId = userId,
            videoId = videoId,
            interactionType =
                InteractionType.INTERACTION_TYPE_LIKE
        )
    }

    suspend fun unlikeVideo(
        userId: String,
        videoId: String
    ): CreateInteractionResponse {
        return createInteraction(
            userId = userId,
            videoId = videoId,
            interactionType =
                InteractionType.INTERACTION_TYPE_UNLIKE
        )
    }

    suspend fun skipVideo(
        userId: String,
        videoId: String
    ): CreateInteractionResponse {
        return createInteraction(
            userId = userId,
            videoId = videoId,
            interactionType =
                InteractionType.INTERACTION_TYPE_SKIP
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalTime::class)
    suspend fun recordWatchSession(
        userId: String,
        videoId: String,
        watchedSeconds: Int,
        completed: Boolean,
        startedAt: Instant,
        endedAt: Instant
    ): RecordWatchSessionResponse {
        require(userId.isNotBlank()) {
            "User ID cannot be blank"
        }

        require(videoId.isNotBlank()) {
            "Video ID cannot be blank"
        }

        require(watchedSeconds >= 0) {
            "Watched seconds cannot be negative"
        }

        require(!endedAt.isBefore(startedAt)) {
            "Watch session cannot end before it starts"
        }

        val request = RecordWatchSessionRequest
            .newBuilder()
            .setUserId(userId)
            .setVideoId(videoId)
            .setWatchedSeconds(watchedSeconds)
            .setCompleted(completed)
            .setStartedAt(startedAt.toString())
            .setEndedAt(endedAt.toString())
            .build()

        return stub.recordWatchSession(request)
    }

    suspend fun getVideoInteractionSummary(
        userId: String,
        videoId: String
    ): GetVideoInteractionSummaryResponse {
        require(userId.isNotBlank()) {
            "User ID cannot be blank"
        }

        require(videoId.isNotBlank()) {
            "Video ID cannot be blank"
        }

        val request = GetVideoInteractionSummaryRequest
            .newBuilder()
            .setVideoId(videoId)
            .setUserId(userId)
            .build()

        return stub.getVideoInteractionSummary(request)
    }
}