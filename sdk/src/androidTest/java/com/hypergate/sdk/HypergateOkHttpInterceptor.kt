package com.hypergate.sdk

import android.app.Activity
import okhttp3.Interceptor
import okhttp3.Response


internal class HypergateOkHttpInterceptor(private val activity: Activity) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val token = Hypergate.requestTokenSync(
            activity,
            "HTTP/${request.url.host}"
        )
        val authenticatedRequest = request.newBuilder()
            .addHeader("Authorization", "Negotiate ${token}")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}