package org.leonardorat.chat.auth


import android.net.Uri
import net.openid.appauth.AuthorizationServiceConfiguration

object AuthConfig {
    const val CLIENT_ID = "611850287327-5probc8bsk4hteug2t3ij01pg2fdmdd0.apps.googleusercontent.com"

    private const val CLIENT_ID_PREFIX = "611850287327-5probc8bsk4hteug2t3ij01pg2fdmdd0"

    const val REDIRECT_SCHEME = "com.googleusercontent.apps.$CLIENT_ID_PREFIX"

    val REDIRECT_URI: Uri =
        Uri.parse("$REDIRECT_SCHEME:/oauth2redirect")

    val AUTH_ENDPOINT: Uri =
        Uri.parse("https://accounts.google.com/o/oauth2/v2/auth")

    val TOKEN_ENDPOINT: Uri =
        Uri.parse("https://oauth2.googleapis.com/token")

    val SERVICE_CONFIG = AuthorizationServiceConfiguration(
        Uri.parse("https://accounts.google.com/o/oauth2/v2/auth"),
        Uri.parse("https://oauth2.googleapis.com/token")
    )
    val SCOPES = listOf(
        "openid",
        "email",
        "profile",
        "https://www.googleapis.com/auth/gmail.readonly",
        "https://www.googleapis.com/auth/gmail.send"
    )
}