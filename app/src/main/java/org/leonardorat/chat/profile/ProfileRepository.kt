package org.leonardorat.chat.profile

import org.leonardorat.chat.data.UserProfileDao
import org.leonardorat.chat.data.UserProfileEntity
import org.leonardorat.chat.gmail.GmailService

import kotlinx.coroutines.flow.Flow

class ProfileRepository(
    private val userProfileDao: UserProfileDao,
    private val gmailService: GmailService
) {
    fun observeProfile(): Flow<UserProfileEntity?> {
        return userProfileDao.observeProfile()
    }

    suspend fun getProfile(): UserProfileEntity? {
        return userProfileDao.getProfile()
    }

    suspend fun createProfile(displayName: String) {
        val email = gmailService.getMyEmail()

        userProfileDao.saveProfile(
            UserProfileEntity(
                displayName = displayName,
                email = email
            )
        )
    }
}