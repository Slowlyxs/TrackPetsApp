package com.trackpets.app.data.remote

import com.trackpets.app.data.datastore.TokenDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenDataStore: TokenDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // Skip auth endpoints
        if (request.url.encodedPath.contains("/auth/")) {
            return chain.proceed(request)
        }

        val token = runBlocking {
            tokenDataStore.getAccessToken().firstOrNull()
        }

        return if (token != null) {
            val authenticatedRequest = request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(authenticatedRequest)
        } else {
            chain.proceed(request)
        }
    }
}
