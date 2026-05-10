package org.leonardorat.chat.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatRoomDao {

    @Query("SELECT * FROM chat_rooms ORDER BY updatedAt DESC")
    fun observeRooms(): Flow<List<ChatRoomEntity>>

    @Query("SELECT * FROM chat_rooms WHERE id = :roomId LIMIT 1")
    suspend fun getRoom(roomId: String): ChatRoomEntity?

    @Query("SELECT * FROM chat_rooms WHERE peerEmail = :peerEmail LIMIT 1")
    suspend fun findByPeerEmail(peerEmail: String): ChatRoomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: ChatRoomEntity)

    @Query("""
        UPDATE chat_rooms 
        SET lastMessageText = :text, lastMessageAt = :createdAt, updatedAt = :createdAt 
        WHERE id = :roomId
    """)
    suspend fun updateLastMessage(
        roomId: String,
        text: String,
        createdAt: Long
    )
}