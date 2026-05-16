package org.leonardorat.chat.auth

import android.content.Context
import net.openid.appauth.AuthState

class TokenStorage(context: Context) {

    private val prefs = context.getSharedPreferences(
        "chat_mail_auth",
        Context.MODE_PRIVATE
    )

    fun readAuthState(): AuthState {
        val json = prefs.getString(KEY_AUTH_STATE, null)

        return if (json.isNullOrBlank()) {
            AuthState()
        } else {
            AuthState.jsonDeserialize(json)
        }
    }

    fun saveAuthState(authState: AuthState) {
        prefs.edit()
            .putString(KEY_AUTH_STATE, authState.jsonSerializeString())
            .apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val KEY_AUTH_STATE = "auth_state"
    }
}