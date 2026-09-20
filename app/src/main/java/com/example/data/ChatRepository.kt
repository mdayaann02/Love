package com.example.data

import kotlinx.coroutines.flow.Flow

class ChatRepository(private val appDao: AppDao) {
    val allMessages: Flow<List<MessageEntity>> = appDao.getAllMessages()
    val friendProfile: Flow<FriendProfile?> = appDao.getFriendProfile()

    suspend fun insertMessage(message: MessageEntity): Long {
        return appDao.insertMessage(message)
    }

    suspend fun updateMessage(message: MessageEntity) {
        appDao.updateMessage(message)
    }

    suspend fun deleteMessage(id: Long) {
        appDao.deleteMessageById(id)
    }

    suspend fun setMessageSaved(id: Long, isSaved: Boolean) {
        appDao.setMessageSaved(id, isSaved)
    }

    suspend fun markMessageRead(id: Long) {
        appDao.markMessageRead(id)
    }

    suspend fun deleteReadEphemeralMessages() {
        appDao.deleteReadEphemeralMessages()
    }

    suspend fun clearUnsavedMessages() {
        appDao.clearUnsavedMessages()
    }

    suspend fun clearAllMessages() {
        appDao.clearAllMessages()
    }

    suspend fun saveFriendProfile(profile: FriendProfile) {
        appDao.saveFriendProfile(profile)
    }
}
