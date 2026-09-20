package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Update
    suspend fun updateMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessageById(id: Long)

    @Query("UPDATE messages SET isSaved = :isSaved WHERE id = :id")
    suspend fun setMessageSaved(id: Long, isSaved: Boolean)

    @Query("UPDATE messages SET isRead = 1, readTimestamp = :readTime WHERE id = :id AND isRead = 0")
    suspend fun markMessageRead(id: Long, readTime: Long = System.currentTimeMillis())

    @Query("DELETE FROM messages WHERE ephemeralMode = 'DELETE_AFTER_READ' AND isSaved = 0 AND isRead = 1")
    suspend fun deleteReadEphemeralMessages()

    @Query("DELETE FROM messages WHERE isSaved = 0")
    suspend fun clearUnsavedMessages()

    @Query("DELETE FROM messages")
    suspend fun clearAllMessages()

    @Query("SELECT COUNT(*) FROM messages WHERE clientMessageId = :clientMsgId AND clientMessageId != ''")
    suspend fun hasMessageWithClientId(clientMsgId: String): Int

    @Query("UPDATE messages SET isSaved = :isSaved WHERE clientMessageId = :clientMsgId")
    suspend fun setMessageSavedByClientId(clientMsgId: String, isSaved: Boolean)

    @Query("UPDATE messages SET isBurned = 1 WHERE clientMessageId = :clientMsgId")
    suspend fun burnMessageByClientId(clientMsgId: String)

    @Query("SELECT * FROM friend_profile WHERE id = 1")
    fun getFriendProfile(): Flow<FriendProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFriendProfile(profile: FriendProfile)
}
