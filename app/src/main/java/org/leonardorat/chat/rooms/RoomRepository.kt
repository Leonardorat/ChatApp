package org.leonardorat.chat.rooms

import org.leonardorat.chat.data.ChatRoomDao
import org.leonardorat.chat.data.ChatRoomEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class RoomRepository(
    private val chatRoomDao: ChatRoomDao
) {
    fun observeRooms(): Flow<List<ChatRoomEntity>> {
        return chatRoomDao.observeRooms()
    }

    suspend fun getRoom(roomId: String): ChatRoomEntity {
        return chatRoomDao.getRoom(roomId)
            ?: error("Room not found: $roomId")
    }

    suspend fun createRoom(
        displayName: String,
        peerEmail: String
    ): String {
        val normalizedEmail = peerEmail.trim().lowercase()
        val existing = chatRoomDao.findByPeerEmail(normalizedEmail)

        if (existing != null) {
            return existing.id
        }

        val now = System.currentTimeMillis()
        val id = UUID.randomUUID().toString()

        val room = ChatRoomEntity(
            id = id,
            displayName = displayName.trim(),
            peerEmail = normalizedEmail,
            createdAt = now,
            updatedAt = now
        )

        chatRoomDao.insertRoom(room)

        return id
    }

    suspend fun findOrCreateByPeerEmail(peerEmail: String): ChatRoomEntity {
        val normalizedEmail = peerEmail.trim().lowercase()

        val existing = chatRoomDao.findByPeerEmail(normalizedEmail)
        if (existing != null) return existing

        val now = System.currentTimeMillis()
        val room = ChatRoomEntity(
            id = UUID.randomUUID().toString(),
            displayName = normalizedEmail,
            peerEmail = normalizedEmail,
            createdAt = now,
            updatedAt = now
        )

        chatRoomDao.insertRoom(room)
        return room
    }

    suspend fun updateLastMessage(
        roomId: String,
        text: String,
        createdAt: Long
    ) {
        chatRoomDao.updateLastMessage(roomId, text, createdAt)
    }
}