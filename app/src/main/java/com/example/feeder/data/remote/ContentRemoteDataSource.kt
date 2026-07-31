package com.example.feeder.data.remote

import com.example.feeder.core.network.GrpcChannelProvider
import com.example.feeder.core.network.GrpcConfig
import com.example.feeder.proto.content.v1.ContentServiceGrpcKt
import com.example.feeder.proto.content.v1.ListFeedVideosRequest
import com.example.feeder.proto.content.v1.ListFeedVideosResponse

class ContentRemoteDataSource {
    private val channel = GrpcChannelProvider.provideChannel(GrpcConfig.CONTENT_SERVICE_PROTO)
    private val stub =
        ContentServiceGrpcKt.ContentServiceCoroutineStub(channel)
    suspend fun listFeedVideos(
        limit:Int,
        offset:Int
    ) : ListFeedVideosResponse {
        val request = ListFeedVideosRequest
            .newBuilder()
            .setLimit(limit)
            .setOffset(offset)
            .build()

        return stub.listFeedVideos(request)
    }
}