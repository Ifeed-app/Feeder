package com.example.feeder.core.network

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor

class JwtClientInterceptor(
    private val accessTokenProvider: () -> String?
) : ClientInterceptor {

    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel
    ): ClientCall<ReqT, RespT> {
        return object :
            ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
                next.newCall(method, callOptions)
            ) {

            override fun start(
                responseListener: ClientCall.Listener<RespT>,
                headers: Metadata
            ) {
                val accessToken = accessTokenProvider()
                    ?.trim()
                    ?.takeIf { it.isNotEmpty() }

                if (accessToken != null) {
                    headers.put(
                        AUTHORIZATION_HEADER,
                        "Bearer $accessToken"
                    )
                }

                super.start(responseListener, headers)
            }
        }
    }

    private companion object {
        val AUTHORIZATION_HEADER: Metadata.Key<String> =
            Metadata.Key.of(
                "authorization",
                Metadata.ASCII_STRING_MARSHALLER
            )
    }
}