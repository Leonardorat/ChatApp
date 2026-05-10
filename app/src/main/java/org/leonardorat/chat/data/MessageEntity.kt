package org.leonardorat.chat.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

object MessageDirection {
    const val INCOMING = "INCOMING"
    const val OUTGOING = "OUTGOING"
}

object MessageStatus {
    const val PENDING = "PENDING"
    const val SENT = "SENT"
    const val FAILED = "FAILED"
    const val RECEIVED = "RECEIVED"
}

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatRoomEntity::class,
            parentColumns = ["id"],
            childColumns = ["roomId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["roomId"]),
        Index(value = ["chatMessageId"], unique = true)
    ]
)
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val roomId: String,
    val chatMessageId: String,
    val gmailMessageId: String?,
    val fromEmail: String,
    val toEmail: String,
    val text: String,
    val createdAt: Long,
    val direction: String,
    val status: String
)