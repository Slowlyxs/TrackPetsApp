package com.trackpets.app.data.remote

import com.trackpets.app.data.datastore.TokenDataStore
import com.trackpets.app.data.dto.auth.RefreshRequest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    private val tokenDataStore: TokenDataStore,
    private val authApiServiceProvider: Provider<AuthApiService>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = runBlocking {
            tokenDataStore.getRefreshToken().firstOrNull()
        } ?: return null

        synchronized(this) {
            val newAccessToken = runBlocking {
                try {
                    val authApiService = authApiServiceProvider.get()
                    val refreshResponse = authApiService.refreshToken(RefreshRequest(refreshToken))
                    
                    if (refreshResponse.isSuccessful) {
                        val body = refreshResponse.body()
                        if (body != null) {
                            tokenDataStore.saveTokens(body.access, body.refresh)
                            body.access
                        } else null
                    } else {
                        tokenDataStore.clearAll()
                        null
                    }
                } catch (e: Exception) {
                    tokenDataStore.clearAll()
                    null
                }
            }

            return if (newAccessToken != null) {
                response.request.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
            } else {
                null
            }
        }
    }
}
