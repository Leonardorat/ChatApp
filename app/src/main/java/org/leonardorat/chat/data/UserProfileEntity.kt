package org.leonardorat.chat.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: String = "me",
    val displayName: String,
    val email: String,
    val createdAt: Long = System.currentTimeMillis()
)