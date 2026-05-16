package org.leonardorat.chat.auth

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenResponse
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthManager(
    context: Context,
    private val tokenStorage: TokenStorage
) {
    private val authService = AuthorizationService(context)

    fun createSignInIntent(): Intent {
        val request = AuthorizationRequest.Builder(
            AuthConfig.SERVICE_CONFIG,
            AuthConfig.CLIENT_ID,
            ResponseTypeValues.CODE,
            AuthConfig.REDIRECT_URI
        )
            .setScope(AuthConfig.SCOPES.joinToString(" "))
            .setAdditionalParameters(
                mapOf(
                    "access_type" to "offline",
                    "prompt" to "select_account consent"
                )
            )
            .build()

        return authService.getAuthorizationRequestIntent(request)
    }

    suspend fun handleAuthorizationResponse(intent: Intent) {
        val response = AuthorizationResponse.fromIntent(intent)
        val exception = AuthorizationException.fromIntent(intent)

        if (response == null) {
            throw AuthorizationRequiredException(
                exception?.errorDescription ?: "Authorization response is missing"
            )
        }

        val authState = tokenStorage.readAuthState()
        authState.update(response, exception)

        val tokenRequest = response.createTokenExchangeRequest()

        val tokenResponse = suspendCancellableCoroutine<TokenResponse> { cont ->
            authService.performTokenRequest(tokenRequest) { tokenResponse, tokenException ->
                if (tokenResponse != null) {
                    cont.resume(tokenResponse)
                } else {
                    cont.resumeWithException(
                        AuthorizationRequiredException(
                            tokenException?.errorDescription ?: "Token exchange failed"
                        )
                    )
                }
            }
        }

        authState.update(tokenResponse, null)
        tokenStorage.saveAuthState(authState)
    }

    suspend fun ensureAuthorized(): Boolean {
        return try {
            freshAccessToken()
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun freshAccessToken(): String {
        val authState = tokenStorage.readAuthState()

        if (!authState.isAuthorized) {
            throw AuthorizationRequiredException()
        }

        return suspendCancellableCoroutine { cont ->
            authState.performActionWithFreshTokens(authService) { accessToken, _, exception ->
                tokenStorage.saveAuthState(authState)

                if (exception != null || accessToken.isNullOrBlank()) {
                    cont.resumeWithException(
                        AuthorizationRequiredException(
                            exception?.errorDescription ?: "Unable to refresh token"
                        )
                    )
                } else {
                    cont.resume(accessToken)
                }
            }
        }
    }

    fun clearAuth() {
        tokenStorage.clear()
    }
}