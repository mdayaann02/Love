package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MessageEntity
import com.example.ui.theme.GlassTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun MessageItem(
    message: MessageEntity,
    onToggleSave: (MessageEntity) -> Unit,
    onBurnImmediately: (Long) -> Unit,
    onMarkRead: (MessageEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = GlassTheme.palette
    val isMe = message.sender == "me"
    var showActionMenu by remember { mutableStateOf(false) }

    // Mark read when displayed
    LaunchedEffect(message.id) {
        if (!message.isRead && !isMe) {
            onMarkRead(message)
        }
    }

    val timeString = remember(message.timestamp) {
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            // If message from friend, show avatar hint
            if (!isMe) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .border(1.dp, palette.glassCardBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👻", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(6.dp))
            }

            // The Message Bubble
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = if (isMe) 20.dp else 4.dp,
                            bottomEnd = if (isMe) 4.dp else 20.dp
                        )
                    )
                    .then(
                        // Snapchat-style Saved Message Highlight
                        if (message.isSaved) {
                            Modifier
                                .background(
                                    if (isMe) palette.snapPrimaryYellow.copy(alpha = 0.35f)
                                    else Color.White.copy(alpha = 0.22f)
                                )
                                .border(
                                    width = 2.dp,
                                    color = palette.savedMessageBorder,
                                    shape = RoundedCornerShape(
                                        topStart = 20.dp,
                                        topEnd = 20.dp,
                                        bottomStart = if (isMe) 20.dp else 4.dp,
                                        bottomEnd = if (isMe) 4.dp else 20.dp
                                    )
                                )
                        } else {
                            Modifier
                                .background(if (isMe) palette.myBubbleBrush else palette.friendBubbleBrush)
                                .border(
                                    width = 1.dp,
                                    color = if (isMe) palette.myBubbleBorder else palette.friendBubbleBorder,
                                    shape = RoundedCornerShape(
                                        topStart = 20.dp,
                                        topEnd = 20.dp,
                                        bottomStart = if (isMe) 20.dp else 4.dp,
                                        bottomEnd = if (isMe) 4.dp else 20.dp
                                    )
                                )
                        }
                    )
                    .combinedClickable(
                        onClick = {
                            // Tap toggles Snapchat Save in Chat
                            onToggleSave(message)
                        },
                        onLongClick = {
                            showActionMenu = !showActionMenu
                        }
                    )
                    .padding(
                        horizontal = if (message.mediaType == "STICKER") 14.dp else 14.dp,
                        vertical = if (message.mediaType == "STICKER") 8.dp else 10.dp
                    )
                    .testTag("message_bubble_${message.id}")
            ) {
                Column {
                    // Ephemeral / Saved status pill on top of bubble
                    if (message.isSaved) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Saved in chat",
                                tint = if (isMe) palette.myBubbleText else palette.snapPrimaryYellow,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "SAVED IN CHAT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp,
                                color = if (isMe) palette.myBubbleText else palette.snapPrimaryYellow
                            )
                        }
                    } else if (message.ephemeralMode == "DELETE_AFTER_READ") {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Ephemeral",
                                tint = palette.ephemeralBurnAccent,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "EPHEMERAL • BURNS AFTER READ",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.ephemeralBurnAccent
                            )
                        }
                    }

                    // Content: Funny Emoji Sticker vs Regular Text
                    if (message.mediaType == "STICKER" && message.emojiSticker != null) {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .align(Alignment.CenterHorizontally),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = message.emojiSticker,
                                fontSize = 64.sp
                            )
                        }
                    } else {
                        Text(
                            text = message.text,
                            color = if (isMe) palette.myBubbleText else palette.friendBubbleText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 21.sp
                        )
                    }

                    // Bottom info: Time & Read status
                    Row(
                        modifier = Modifier
                            .align(if (isMe) Alignment.End else Alignment.Start)
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = timeString,
                            color = if (isMe) palette.myBubbleText.copy(alpha = 0.7f)
                            else palette.textTertiary,
                            fontSize = 10.sp
                        )

                        if (isMe) {
                            Spacer(modifier = Modifier.width(4.dp))
                            // Snapchat style read status: solid square if sent, hollow if opened
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (message.isRead) palette.snapPrimaryYellow.copy(alpha = 0.8f)
                                        else Color.White.copy(alpha = 0.5f)
                                    )
                            )
                        }
                    }
                }
            }
        }

        // Expanded Action Drawer on Long Press
        AnimatedVisibility(
            visible = showActionMenu,
            enter = fadeIn(tween(180)),
            exit = fadeOut(tween(180))
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.glassCardBackground)
                    .border(1.dp, palette.glassCardBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Save/Unsave Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .combinedClickable(
                            onClick = {
                                onToggleSave(message)
                                showActionMenu = false
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (message.isSaved) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save message",
                        tint = palette.snapPrimaryYellow,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (message.isSaved) "Unsave" else "Keep in Chat",
                        color = palette.textPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Sudden Burn Now Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.ephemeralBurnAccent.copy(alpha = 0.2f))
                        .combinedClickable(
                            onClick = {
                                onBurnImmediately(message.id)
                                showActionMenu = false
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Burn message",
                        tint = palette.ephemeralBurnAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Burn Now",
                        color = palette.ephemeralBurnAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
