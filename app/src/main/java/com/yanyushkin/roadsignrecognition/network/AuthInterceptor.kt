package com.yanyushkin.roadsignrecognition.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val headers = originalRequest.headers.newBuilder()
            .add("Authorization", "Token 582fec9a583d78b01d4ab2597d06a7637af7d852")
            .build()

        val newRequest = originalRequest.newBuilder()
            .headers(headers)
            .build()

        return chain.proceed(newRequest)
    }
}