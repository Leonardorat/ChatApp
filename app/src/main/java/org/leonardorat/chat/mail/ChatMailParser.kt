package org.leonardorat.chat.mail

import android.util.Base64
//import .mail.ChatMessage
import org.leonardorat.chat.gmail.GmailPayload
import org.leonardorat.chat.gmail.GmailMessage

class ChatMailParser {

    fun parse(message: GmailMessage): ChatMessage? {
        val payload = message.payload ?: return null

        val app = header(payload, "X-Chat-App")
        val type = header(payload, "X-Chat-Type")
        val chatMessageId = header(payload, "X-Chat-Message-Id")

        if (app != "ChatMail") return null
        if (type != "text") return null
        if (chatMessageId.isNullOrBlank()) return null

        val bodyText = findBodyText(payload) ?: return null

        return ChatMessage(
            chatMessageId = chatMessageId,
            from = header(payload, "From") ?: "unknown",
            to = header(payload, "To") ?: "unknown",
            text = bodyText,
            createdAt = header(payload, "X-Chat-Created-At")?.toLongOrNull()
                ?: System.currentTimeMillis(),
            gmailMessageId = message.id
        )
    }

    private fun header(payload: GmailPayload, name: String): String? {
        return payload.headers.firstOrNull {
            it.name.equals(name, ignoreCase = true)
        }?.value
    }

    private fun findBodyText(payload: GmailPayload): String? {
        payload.body?.data?.let { data ->
            return decodeBase64Url(data)
        }

        for (part in payload.parts) {
            val found = findBodyText(part)
            if (found != null) return found
        }

        return null
    }

    private fun decodeBase64Url(data: String): String {
        val bytes = Base64.decode(
            data,
            Base64.URL_SAFE or Base64.NO_WRAP
        )
        return String(bytes, Charsets.UTF_8)
    }
}