package com.example.feeder.data.remote


import com.example.feeder.core.network.GrpcChannelProvider
import com.example.feeder.core.network.GrpcConfig
import com.example.feeder.proto.recommendation.v1.GetRecommendationsRequest
import com.example.feeder.proto.recommendation.v1.GetRecommendationsResponse
import com.example.feeder.proto.recommendation.v1.RecommendationServiceGrpcKt
class RecommendationRemoteDataSource {

    private val channel =
        GrpcChannelProvider.provideChannel(
            GrpcConfig.RECOMMENDATION_SERVICE_PROTO
        )

    private val stub =
        RecommendationServiceGrpcKt
            .RecommendationServiceCoroutineStub(channel)

    suspend fun getRecommendations(
        userId: String?,
        limit: Int
    ): GetRecommendationsResponse {

        val request = GetRecommendationsRequest
            .newBuilder()
            .setUserId(userId)
            .setLimit(limit)
            .build()

        return stub.getRecommendations(request)
    }
}