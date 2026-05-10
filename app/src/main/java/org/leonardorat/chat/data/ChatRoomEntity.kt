package org.leonardorat.chat.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat_rooms",
    indices = [
        Index(value = ["peerEmail"], unique = true)
    ]
)
data class ChatRoomEntity(
    @PrimaryKey
    val id: String,
    val displayName: String,
    val peerEmail: String,
    val createdAt: Long,
    val updatedAt: Long,
    val lastMessageText: String? = null,
    val lastMessageAt: Long? = null
)