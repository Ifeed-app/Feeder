package com.example.feeder.data.remote

import com.example.feeder.core.network.GrpcChannelProvider
import io.grpc.ManagedChannel
import user.v1.Auth
import user.v1.AuthServiceGrpcKt

class AuthRemoteDataSource {
    private val channel: ManagedChannel = GrpcChannelProvider.provideChannel()
    private val stub = AuthServiceGrpcKt.AuthServiceCoroutineStub(channel)

    suspend fun signUp(
        email : String,
        username : String,
        password : String
    ) : Auth.SignUpResponse {
        val request = Auth.SignUpRequest.newBuilder()
            .setEmail(email)
            .setUsername(username)
            .setPassword(password)
            .build()

        return stub.signUp(request)
    }
}