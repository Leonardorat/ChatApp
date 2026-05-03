package org.leonardorat.chat.mail

import android.util.Base64
import org.leonardorat.chat.mail.ChatMessage
import org.leonardorat.chat.gmail.GmailPayload
import org.leonardorat.chat.gmail.GmailMessage

class ChatMailParser {

    fun parse(message: GmailMessage): ChatMessage? {
        val payload = message.payload ?: return null

        val subject = header(payload, "Subject")
        val marker = header(payload, "X-Chat-Mail-Test")


        if (subject != "[ChatMailTest]" || marker != "1") {
            return null
        }

        val bodyText = findBodyText(payload) ?: return null

        return ChatMessage(
            from = header(payload, "From") ?: "unknown",
            to = header(payload, "To") ?: "unknown",
            text = bodyText,
            createdAt = header(payload, "X-Chat-Created-At")?.toLongOrNull() ?: 0L,
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