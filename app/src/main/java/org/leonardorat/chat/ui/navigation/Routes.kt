package org.leonardorat.chat.ui.navigation

object Routes {
    const val PROFILE = "profile"
    const val ROOMS = "rooms"
    const val CREATE_ROOM = "create_room"
    const val CHAT = "chat/{roomId}"
    const val AUTH_CHECK = "auth_check"
    const val AUTH = "auth"

    fun chat(roomId: String): String {
        return "chat/$roomId"
    }
}