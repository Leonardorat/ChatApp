package org.leonardorat.chat.auth


import android.net.Uri

object AuthConfig {
    const val CLIENT_ID = "-"

    private const val CLIENT_ID_PREFIX = "-"

    const val REDIRECT_SCHEME = "com.googleusercontent.apps.$CLIENT_ID_PREFIX"

    val REDIRECT_URI: Uri =
        Uri.parse("$REDIRECT_SCHEME:/-")

    val AUTH_ENDPOINT: Uri =
        Uri.parse("-")

    val TOKEN_ENDPOINT: Uri =
        Uri.parse("-")

    val SCOPES = listOf(
        "-",
        "-"
    )
}