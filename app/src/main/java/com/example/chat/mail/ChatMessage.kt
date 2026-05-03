package org.leonardorat.chat.mail


data class ChatMessage(
    val from: String,
    val to: String,
    val text: String,
    val createdAt: Long,
    val gmailMessageId: String? = null
)