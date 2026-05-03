package org.leonardorat.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import org.leonardorat.chat.auth.AuthManager
import org.leonardorat.chat.gmail.GmailService
import org.leonardorat.chat.mail.ChatMessage
import org.leonardorat.chat.ui.theme.AppScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var authManager: AuthManager
    private lateinit var gmailService: GmailService

    private var authorized by mutableStateOf(false)
    private var status by mutableStateOf("Not connected")
    private var fromEmail by mutableStateOf("")
    private var toEmail by mutableStateOf("")
    private var inputText by mutableStateOf("")

    private val messages = mutableStateListOf<ChatMessage>()

    private val authLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            authManager.handleAuthorizationResult(result.data) { success, error ->
                authorized = success
                status = if (success) {
                    "Gmail connected"
                } else {
                    "Auth error: ${error ?: "unknown"}"
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authManager = AuthManager(this)
        gmailService = GmailService(authManager)
        authorized = authManager.isAuthorized()

        setContent {
            AppScreen(
                authorized = authorized,
                status = status,
                fromEmail = fromEmail,
                toEmail = toEmail,
                inputText = inputText,
                messages = messages,
                onConnectClick = {
                    authLauncher.launch(authManager.createAuthorizationIntent())
                },
                onFromChange = { fromEmail = it },
                onToChange = { toEmail = it },
                onInputChange = { inputText = it },
                onRefreshClick = {
                    refreshMessages()
                },
                onSendClick = {
                    sendMessage()
                }
            )
        }
    }

    private fun refreshMessages() {
        lifecycleScope.launch {
            try {
                status = "Loading messages..."

                val loaded = gmailService.loadChatMessages()

                messages.clear()
                messages.addAll(loaded.sortedBy { it.createdAt })

                status = "Loaded ${loaded.size} messages"
            } catch (e: Exception) {
                status = "Error: ${e::class.simpleName}: ${e.message}"
            }
        }
    }

    private fun sendMessage() {
        val from = fromEmail.trim()
        val to = toEmail.trim()
        val text = inputText.trim()

        if (from.isBlank() || to.isBlank() || text.isBlank()) {
            status = "Fill from, to and message"
            return
        }

        lifecycleScope.launch {
            try {
                status = "Sending..."

                gmailService.sendChatMessage(
                    from = from,
                    to = to,
                    text = text
                )

                inputText = ""
                status = "Sent"

                refreshMessages()
            } catch (e: Exception) {
                status = "Send error: ${e.message}"
            }
        }
    }
}