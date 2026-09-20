package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassTheme

@Composable
fun ChatComposer(
    text: String,
    onTextChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    ephemeralMode: String,
    onToggleEphemeral: () -> Unit,
    isEmojiDrawerOpen: Boolean,
    onToggleEmojiDrawer: () -> Unit,
    onSimulateIncomingMessage: () -> Unit,
    onGoogleMessagesClick: () -> Unit = {},
    onLovePing: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val palette = GlassTheme.palette

    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // Ephemeral Banner Hint
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp, start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onToggleEphemeral() }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = if (ephemeralMode == "DELETE_AFTER_READ") Icons.Default.LocalFireDepartment else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (ephemeralMode == "DELETE_AFTER_READ") palette.ephemeralBurnAccent else palette.snapPrimaryYellow,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (ephemeralMode == "DELETE_AFTER_READ") "Mode: Suddenly Delete After Read" else "Mode: Keep in Chat",
                    color = palette.textSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Quick Real-Time Love Ping (Heart)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFF4081).copy(alpha = 0.20f))
                        .border(1.dp, Color(0xFFFF4081).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable { onLovePing() }
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                        .testTag("composer_love_ping_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "❤️",
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Love Ping",
                            color = Color(0xFFFF80AB),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Quick SMS in Google Messages
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1A73E8).copy(alpha = 0.20f))
                        .border(1.dp, Color(0xFF1A73E8).copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                        .clickable { onGoogleMessagesClick() }
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                        .testTag("composer_google_messages_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = "SMS via Google Messages",
                            tint = Color(0xFF64B5F6),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Google Messages",
                            color = Color(0xFF90CAF9),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Quick Girlfriend Incoming Ping (Solo test simulator)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.snapPrimaryYellow.copy(alpha = 0.18f))
                        .border(1.dp, palette.snapPrimaryYellow.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable { onSimulateIncomingMessage() }
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                        .testTag("simulate_friend_ping_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Incoming Ping",
                            tint = palette.snapPrimaryYellow,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Babe Snap",
                            color = palette.snapPrimaryYellow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Main Liquid Glass Composer Capsule
        LiquidGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("chat_composer_card"),
            shape = RoundedCornerShape(30.dp),
            backgroundColor = palette.inputBarBackground,
            borderColor = palette.glassCardBorder,
            borderWidth = 1.2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Funny Emoji Pack Toggle Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (isEmojiDrawerOpen) palette.snapPrimaryYellow
                            else Color.White.copy(alpha = 0.12f)
                        )
                        .border(
                            1.dp,
                            if (isEmojiDrawerOpen) palette.snapPrimaryYellow else Color.White.copy(alpha = 0.25f),
                            CircleShape
                        )
                        .clickable { onToggleEmojiDrawer() }
                        .testTag("emoji_drawer_toggle"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🤪",
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Text Field with Ghost Placeholder
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (text.isEmpty()) {
                        Text(
                            text = "Send a chat or funny sticker...",
                            color = palette.textTertiary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    BasicTextField(
                        value = text,
                        onValueChange = onTextChanged,
                        textStyle = TextStyle(
                            color = palette.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(palette.snapPrimaryYellow),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("chat_input_field")
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Send Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (text.isNotBlank()) palette.snapPrimaryYellow
                            else Color.White.copy(alpha = 0.10f)
                        )
                        .border(
                            1.dp,
                            if (text.isNotBlank()) palette.snapPrimaryYellow else Color.White.copy(alpha = 0.15f),
                            CircleShape
                        )
                        .clickable(enabled = text.isNotBlank()) {
                            onSendMessage()
                        }
                        .testTag("send_message_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = if (text.isNotBlank()) palette.onAccentText else palette.textTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
