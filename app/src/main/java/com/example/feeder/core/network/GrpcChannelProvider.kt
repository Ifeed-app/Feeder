package com.example.feeder.core.network

import io.grpc.ManagedChannel
import io.grpc.okhttp.OkHttpChannelBuilder
import java.util.concurrent.TimeUnit

object GrpcChannelProvider {
    fun provideChannel(): ManagedChannel {
        return OkHttpChannelBuilder.forAddress(GrpcConfig.HOST, GrpcConfig.PORT)
            .usePlaintext().build()
    }

    fun shutDown(channel: ManagedChannel) {
        channel.shutdown()
        channel.awaitTermination(5, TimeUnit.SECONDS)
    }
}