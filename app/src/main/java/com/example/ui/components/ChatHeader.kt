package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FriendProfile
import com.example.ui.theme.GlassTheme
import com.example.ui.theme.TimeOfDay

@Composable
fun ChatHeader(
    profile: FriendProfile?,
    isFriendTyping: Boolean,
    currentTimeOfDay: TimeOfDay,
    ephemeralMode: String,
    isRealtimeConnected: Boolean = true,
    onProfileClick: () -> Unit,
    onEphemeralToggleClick: () -> Unit,
    onTimeOfDayToggleClick: () -> Unit,
    onGoogleMessagesClick: () -> Unit = {},
    onLovePingClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val palette = GlassTheme.palette
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_halo")
    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Main Glass Header Bar
        LiquidGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("chat_header_card"),
            shape = RoundedCornerShape(26.dp),
            backgroundColor = palette.glassHeaderBackground,
            borderColor = palette.glassHeaderBorder,
            borderWidth = 1.2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Friend Avatar + Status
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onProfileClick() }
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Glass Avatar Circle
                    Box(
                        modifier = Modifier.size(46.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Outer glowing halo
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            palette.snapPrimaryYellow.copy(alpha = haloAlpha),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                        // Inner Glass Pill
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.20f))
                                .border(1.5.dp, palette.snapPrimaryYellow, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile?.avatarEmoji ?: "👻",
                                fontSize = 22.sp
                            )
                        }

                        // Online indicator dot
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(Color(0xFF00FF7F))
                                .border(1.5.dp, Color.Black, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Friend Name, Streak and Status
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = profile?.name ?: "Alex",
                                color = palette.textPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            // Streak badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(palette.snapPrimaryYellow.copy(alpha = 0.25f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${profile?.streakEmoji ?: "🔥"} ${profile?.streakCount ?: 142}",
                                        color = palette.snapPrimaryYellow,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Status / Typing indicator
                        AnimatedVisibility(
                            visible = isFriendTyping,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "typing",
                                    color = palette.snapPrimaryYellow,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                BouncingTypingDots(
                                    dotColor = palette.snapPrimaryYellow
                                )
                            }
                        }

                        if (!isFriendTyping) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(if (isRealtimeConnected) Color(0xFF00E676) else Color(0xFFFFB74D))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isRealtimeConnected) "Live 2-Way Realtime" else "Connecting...",
                                    color = if (isRealtimeConnected) Color(0xFF69F0AE) else palette.textSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isRealtimeConnected) FontWeight.Medium else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Right: Controls (Ephemeral Mode Pill & Time of Day Chip)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Time of Day Ambient Chip (Tap to shift time of day colors!)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .border(1.dp, palette.glassCardBorder, RoundedCornerShape(16.dp))
                            .clickable { onTimeOfDayToggleClick() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("time_of_day_chip"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentTimeOfDay.icon,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Ephemeral Mode Switch Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (ephemeralMode == "DELETE_AFTER_READ") palette.ephemeralBurnAccent.copy(alpha = 0.25f)
                                else palette.snapPrimaryYellow.copy(alpha = 0.20f)
                            )
                            .border(
                                1.dp,
                                if (ephemeralMode == "DELETE_AFTER_READ") palette.ephemeralBurnAccent
                                else palette.snapPrimaryYellow,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { onEphemeralToggleClick() }
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                            .testTag("ephemeral_mode_pill")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (ephemeralMode == "DELETE_AFTER_READ") "🔥 Sudden" else "🔒 Keep",
                                color = if (ephemeralMode == "DELETE_AFTER_READ") palette.ephemeralBurnAccent else palette.snapPrimaryYellow,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Love Ping button (Sends an instant heart ping in real-time)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFFF4081).copy(alpha = 0.20f))
                            .border(1.dp, Color(0xFFFF4081).copy(alpha = 0.6f), CircleShape)
                            .clickable { onLovePingClick() }
                            .padding(6.dp)
                            .testTag("header_love_ping_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "❤️",
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Google Messages direct link button
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFF1A73E8).copy(alpha = 0.20f))
                            .border(1.dp, Color(0xFF1A73E8).copy(alpha = 0.5f), CircleShape)
                            .clickable { onGoogleMessagesClick() }
                            .padding(7.dp)
                            .testTag("header_google_messages_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = "Text in Google Messages",
                            tint = Color(0xFF64B5F6),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = { onProfileClick() },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("header_more_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Profile & Settings",
                            tint = palette.textSecondary
                        )
                    }
                }
            }
        }
    }
}
