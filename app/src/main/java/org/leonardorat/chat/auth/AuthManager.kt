package org.leonardorat.chat.auth

import android.content.Context
import android.content.Intent
import androidx.core.content.edit
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthManager(
    private val context: Context
) {
    private val prefs =
        context.getSharedPreferences("auth_state", Context.MODE_PRIVATE)

    private val authService = AuthorizationService(context)

    private val serviceConfig = AuthorizationServiceConfiguration(
        AuthConfig.AUTH_ENDPOINT,
        AuthConfig.TOKEN_ENDPOINT
    )

    fun createAuthorizationIntent(): Intent {
        val request = AuthorizationRequest.Builder(
            serviceConfig,
            AuthConfig.CLIENT_ID,
            ResponseTypeValues.CODE,
            AuthConfig.REDIRECT_URI
        )
            .setScopes(AuthConfig.SCOPES)
            .build()

        return authService.getAuthorizationRequestIntent(request)
    }

    fun handleAuthorizationResult(
        data: Intent?,
        onResult: (Boolean, String?) -> Unit
    ) {
        if (data == null) {
            onResult(false, "No auth result")
            return
        }

        val response = AuthorizationResponse.fromIntent(data)
        val exception = AuthorizationException.fromIntent(data)

        if (response == null) {
            onResult(false, exception?.errorDescription ?: "Authorization cancelled")
            return
        }

        val authState = AuthState(response, exception)

        authService.performTokenRequest(
            response.createTokenExchangeRequest()
        ) { tokenResponse, tokenException ->
            authState.update(tokenResponse, tokenException)
            saveAuthState(authState)

            if (authState.isAuthorized) {
                onResult(true, null)
            } else {
                onResult(false, tokenException?.errorDescription ?: "Token exchange failed")
            }
        }
    }

    suspend fun freshAccessToken(): String {
        val authState = readAuthState()

        if (!authState.isAuthorized) {
            throw IllegalStateException("User is not authorized")
        }

        return suspendCancellableCoroutine { continuation ->
            authState.performActionWithFreshTokens(authService) { accessToken, _, exception ->
                saveAuthState(authState)

                when {
                    exception != null -> continuation.resumeWithException(exception)
                    accessToken == null -> continuation.resumeWithException(
                        IllegalStateException("Access token is null")
                    )
                    else -> continuation.resume(accessToken)
                }
            }
        }
    }

    fun isAuthorized(): Boolean = readAuthState().isAuthorized

    fun logout() {
        prefs.edit { remove(KEY_AUTH_STATE) }
    }

    private fun saveAuthState(authState: AuthState) {
        prefs.edit {
            putString(KEY_AUTH_STATE, authState.jsonSerializeString())
        }
    }

    private fun readAuthState(): AuthState {
        val json = prefs.getString(KEY_AUTH_STATE, null)
        return if (json != null) {
            AuthState.jsonDeserialize(json)
        } else {
            AuthState()
        }
    }

    companion object {
        private const val KEY_AUTH_STATE = "auth_state_json"
    }
}