package org.leonardorat.chat.messages

import org.leonardorat.chat.data.MessageDao
import org.leonardorat.chat.data.MessageDirection
import org.leonardorat.chat.data.MessageEntity
import org.leonardorat.chat.data.MessageStatus
import org.leonardorat.chat.gmail.GmailService
import org.leonardorat.chat.profile.ProfileRepository
import org.leonardorat.chat.rooms.RoomRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class MessageRepository(
    private val messageDao: MessageDao,
    private val roomRepository: RoomRepository,
    private val profileRepository: ProfileRepository,
    private val gmailService: GmailService
) {
    fun observeMessages(roomId: String): Flow<List<MessageEntity>> {
        return messageDao.observeMessages(roomId)
    }

    suspend fun sendMessage(
        roomId: String,
        text: String
    ) {
        val profile = profileRepository.getProfile()
            ?: error("Profile is not created")

        val room = roomRepository.getRoom(roomId)

        val localMessageId = UUID.randomUUID().toString()
        val chatMessageId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        val pending = MessageEntity(
            id = localMessageId,
            roomId = roomId,
            chatMessageId = chatMessageId,
            gmailMessageId = null,
            fromEmail = profile.email,
            toEmail = room.peerEmail,
            text = text,
            createdAt = now,
            direction = MessageDirection.OUTGOING,
            status = MessageStatus.PENDING
        )

        messageDao.insertMessage(pending)
        roomRepository.updateLastMessage(roomId, text, now)

        try {
            gmailService.sendChatMessage(
                from = profile.email,
                to = room.peerEmail,
                text = text,
                chatMessageId = chatMessageId
            )

            messageDao.updateStatus(localMessageId, MessageStatus.SENT)
        } catch (e: Exception) {
            messageDao.updateStatus(localMessageId, MessageStatus.FAILED)
            throw e
        }
    }

    suspend fun syncLatestMessages() {
        val profile = profileRepository.getProfile()
            ?: error("Profile is not created")

        val loadedMessages = gmailService.loadChatMessages()

        for (message in loadedMessages) {
            val existing = messageDao.findByChatMessageId(message.chatMessageId)
            if (existing != null) continue

            val peerEmail = if (normalizeEmail(message.from) == normalizeEmail(profile.email)) {
                normalizeEmail(message.to)
            } else {
                normalizeEmail(message.from)
            }

            val room = roomRepository.findOrCreateByPeerEmail(peerEmail)

            val direction =
                if (normalizeEmail(message.from) == normalizeEmail(profile.email)) {
                    MessageDirection.OUTGOING
                } else {
                    MessageDirection.INCOMING
                }

            val status =
                if (direction == MessageDirection.OUTGOING) {
                    MessageStatus.SENT
                } else {
                    MessageStatus.RECEIVED
                }

            val entity = MessageEntity(
                id = UUID.randomUUID().toString(),
                roomId = room.id,
                chatMessageId = message.chatMessageId,
                gmailMessageId = message.gmailMessageId,
                fromEmail = message.from,
                toEmail = message.to,
                text = message.text,
                createdAt = message.createdAt,
                direction = direction,
                status = status
            )

            messageDao.insertMessage(entity)
            roomRepository.updateLastMessage(room.id, message.text, message.createdAt)
        }
    }

    private fun normalizeEmail(value: String): String {
        return value
            .substringAfter("<", value)
            .substringBefore(">", value)
            .trim()
            .lowercase()
    }
}