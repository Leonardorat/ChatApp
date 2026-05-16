package org.leonardorat.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.leonardorat.chat.auth.AuthManager
import org.leonardorat.chat.data.AppDatabase
import org.leonardorat.chat.gmail.GmailService
import org.leonardorat.chat.messages.MessageRepository
import org.leonardorat.chat.profile.ProfileRepository
import org.leonardorat.chat.rooms.RoomRepository
import org.leonardorat.chat.ui.navigation.AppNavHost
import org.leonardorat.chat.ui.theme.ChatTheme
import org.leonardorat.chat.auth.TokenStorage

class MainActivity : ComponentActivity() {

    private lateinit var authManager: AuthManager
    private lateinit var gmailService: GmailService
    private lateinit var profileRepository: ProfileRepository
    private lateinit var roomRepository: RoomRepository
    private lateinit var messageRepository: MessageRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authManager = AuthManager(this)
        gmailService = GmailService(authManager)

        val db = AppDatabase.get(this)

        profileRepository = ProfileRepository(
            userProfileDao = db.userProfileDao(),
            gmailService = gmailService
        )

        roomRepository = RoomRepository(
            chatRoomDao = db.chatRoomDao()
        )

        messageRepository = MessageRepository(
            messageDao = db.messageDao(),
            roomRepository = roomRepository,
            profileRepository = profileRepository,
            gmailService = gmailService
        )

        setContent {
            ChatTheme {
                AppNavHost(
                    profileRepository = profileRepository,
                    roomRepository = roomRepository,
                    messageRepository = messageRepository
                )
            }
        }
    }
}