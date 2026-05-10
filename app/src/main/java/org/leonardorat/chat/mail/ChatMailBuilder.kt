package org.leonardorat.chat.mail


import android.util.Base64
//import org.json.JSONObject

class ChatMailBuilder {

    fun buildRawEmail(
        from: String,
        to: String,
        text: String,
        chatMessageId: String
    ): String {
        val createdAt = System.currentTimeMillis()

        val mimeMessage = buildString {
            append("From: ").append(from).append("\r\n")
            append("To: ").append(to).append("\r\n")
            append("Subject: [ChatMail]").append("\r\n")
            append("MIME-Version: 1.0").append("\r\n")
            append("X-Chat-App: ChatMail").append("\r\n")
            append("X-Chat-Version: 1").append("\r\n")
            append("X-Chat-Type: text").append("\r\n")
            append("X-Chat-Message-Id: ").append(chatMessageId).append("\r\n")
            append("X-Chat-Created-At: ").append(createdAt).append("\r\n")
            append("Content-Type: text/plain; charset=UTF-8").append("\r\n")
            append("Content-Transfer-Encoding: 8bit").append("\r\n")
            append("\r\n")
            append(text)
        }

        return Base64.encodeToString(
            mimeMessage.toByteArray(Charsets.UTF_8),
            Base64.URL_SAFE or Base64.NO_WRAP
        )
    }
}