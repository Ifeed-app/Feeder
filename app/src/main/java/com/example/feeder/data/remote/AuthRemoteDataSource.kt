package com.example.feeder.data.remote

import com.example.feeder.core.network.GrpcChannelProvider
import com.example.feeder.core.network.GrpcConfig
import io.grpc.ManagedChannel
import user.v1.Auth
import user.v1.AuthServiceGrpcKt

class AuthRemoteDataSource {
    private val channel: ManagedChannel = GrpcChannelProvider.provideChannel(GrpcConfig.USER_SERVICE_PROTO)
    private val stub = AuthServiceGrpcKt.AuthServiceCoroutineStub(channel)

    suspend fun signUp(
        email: String,
        username: String,
        password: String,
        birthYear: Int,
        gender: String,
        countryCode: String,
        preferredLanguage: String,
        interests: List<String>
    ) : Auth.SignUpResponse {
        val request = Auth.SignUpRequest.newBuilder()
            .setEmail(email.trim().lowercase())
            .setUsername(username.trim())
            .setPassword(password)
            .setBirthYear(birthYear)
            .setGender(gender)
            .setCountryCode(countryCode)
            .setPreferredLanguage(preferredLanguage)
            .addAllInterests(interests)
            .build()

        return stub.signUp(request)
    }

    suspend fun signIn (
        identifier: String,
        password: String
    ) : Auth.SignInResponse {
        var request = Auth.SignInRequest.newBuilder()
            .setIdentifier(identifier.trim())
            .setPassword(password)
            .build()
        return stub.signIn(request)
    }
}