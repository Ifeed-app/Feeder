package com.example.feeder.core.network

import io.grpc.ClientInterceptor
import io.grpc.ManagedChannel
import io.grpc.okhttp.OkHttpChannelBuilder
import java.util.concurrent.TimeUnit

object GrpcChannelProvider {
    fun provideChannel(port: Int, vararg interceptor: ClientInterceptor): ManagedChannel {
        val builder = OkHttpChannelBuilder
            .forAddress(GrpcConfig.HOST, port)
            .usePlaintext()

        if (interceptor.isNotEmpty()) {
            builder.intercept(*interceptor)
        }

        return builder.build()
    }

    fun shutDown(channel: ManagedChannel) {
        channel.shutdown()
        channel.awaitTermination(5, TimeUnit.SECONDS)
    }
}