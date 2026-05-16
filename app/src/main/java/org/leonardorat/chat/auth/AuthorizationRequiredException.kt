package org.leonardorat.chat.auth

class AuthorizationRequiredException(
    message: String = "User is not authorized"
) : Exception(message)