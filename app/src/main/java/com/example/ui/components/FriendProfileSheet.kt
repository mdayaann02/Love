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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.ContentCopy
import com.example.data.FriendProfile
import com.example.ui.theme.GlassTheme
import com.example.ui.theme.TimeOfDay

@Composable
fun FriendProfileSheet(
    profile: FriendProfile?,
    googleUser: com.example.data.GoogleUserData = com.example.data.GoogleUserData(),
    currentTimeOfDay: TimeOfDay,
    onTimeOfDaySelected: (String) -> Unit,
    ephemeralMode: String,
    onEphemeralModeSelected: (String) -> Unit,
    onUpdateProfile: (name: String, handle: String, avatar: String, streak: Int, phone: String) -> Unit,
    onTestHapticFeedback: () -> Unit,
    onClearUnsavedMessages: () -> Unit,
    isRealtimeConnected: Boolean = true,
    onSwitchRole: (String) -> Unit = {},
    onUpdateCoupleCode: (String) -> Unit = {},
    onLovePing: (String) -> Unit = {},
    onSignInGoogleDemo: (email: String, name: String) -> Unit = { _, _ -> },
    onSignOutGoogle: () -> Unit = {},
    onOpenGoogleMessages: (prefillText: String) -> Unit = {},
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = GlassTheme.palette
    var showEditDialog by remember { mutableStateOf(false) }

    var editName by remember { mutableStateOf(profile?.name ?: "My Babe 💖") }
    var editHandle by remember { mutableStateOf(profile?.handle ?: "my.girlfriend") }
    var editAvatar by remember { mutableStateOf(profile?.avatarEmoji ?: "👸") }
    var editStreak by remember { mutableStateOf((profile?.streakCount ?: 365).toString()) }
    var editPhone by remember { mutableStateOf(profile?.phoneNumber ?: "+15551234567") }
    var editCoupleCode by remember { mutableStateOf(profile?.coupleSyncCode ?: "DAYAN-LOVE-2026") }

    val avatarOptions = listOf("👻", "⚡", "👑", "💅", "🔥", "🦄", "👽", "🐱", "🐶", "🥑")

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(text = "Edit Friend Profile & Phone", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Friend Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editHandle,
                        onValueChange = { editHandle = it },
                        label = { Text("Snap Username") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Google Messages Phone Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editStreak,
                        onValueChange = { editStreak = it },
                        label = { Text("Streak Days") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editCoupleCode,
                        onValueChange = { editCoupleCode = it.uppercase().replace(" ", "-") },
                        label = { Text("Couple Room Code (Live Sync Topic)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Choose Bitmoji / Avatar:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        avatarOptions.take(5).forEach { emo ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (editAvatar == emo) palette.snapPrimaryYellow.copy(alpha = 0.4f) else Color.Transparent)
                                    .border(1.dp, if (editAvatar == emo) palette.snapPrimaryYellow else Color.Gray.copy(alpha = 0.3f), CircleShape)
                                    .clickable { editAvatar = emo },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emo, fontSize = 20.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val streak = editStreak.toIntOrNull() ?: 365
                        onUpdateProfile(editName, editHandle, editAvatar, streak, editPhone)
                        if (editCoupleCode.isNotBlank()) {
                            onUpdateCoupleCode(editCoupleCode)
                        }
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = palette.snapPrimaryYellow)
                ) {
                    Text("Save", color = palette.onAccentText, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    LiquidGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .testTag("friend_profile_sheet"),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        backgroundColor = palette.glassHeaderBackground,
        borderColor = palette.glassHeaderBorder,
        borderWidth = 1.5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Drag Handle & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Couple Space",
                        tint = Color(0xFFFF4081),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "COUPLE SPACE & REAL-TIME LINK",
                        color = Color(0xFFFF80AB),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Profile",
                        tint = palette.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Girlfriend Avatar Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, palette.glassCardBorder, RoundedCornerShape(24.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Giant Glass Avatar
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .border(2.dp, Color(0xFFFF4081), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile?.avatarEmoji ?: "👸",
                            fontSize = 46.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = profile?.name ?: "My Babe 💖",
                            color = palette.textPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = { showEditDialog = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = palette.snapPrimaryYellow,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = "@${profile?.handle ?: "my.girlfriend"}",
                        color = palette.textSecondary,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Streak Flame Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFFF4081).copy(alpha = 0.25f))
                            .border(1.dp, Color(0xFFFF4081), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "❤️",
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${profile?.streakCount ?: 365} Days Streak! Couple Forever 💕",
                                color = Color(0xFFFF80AB),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Real-Time Couple 2-Way Sync Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, Color(0xFFFF4081).copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                    .padding(16.dp)
                    .testTag("couple_sync_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Real-time Sync",
                                tint = Color(0xFFFF4081),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "REAL-TIME COUPLE SYNC",
                                color = Color(0xFFFF80AB),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.8.sp
                            )
                        }

                        // Live Status Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isRealtimeConnected) Color(0xFF00E676).copy(alpha = 0.20f) else Color(0xFFFFB74D).copy(alpha = 0.20f))
                                .border(1.dp, if (isRealtimeConnected) Color(0xFF00E676) else Color(0xFFFFB74D), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (isRealtimeConnected) Color(0xFF00E676) else Color(0xFFFFB74D))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isRealtimeConnected) "Live Stream" else "Connecting",
                                    color = if (isRealtimeConnected) Color(0xFF69F0AE) else Color(0xFFFFB74D),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "WHO IS USING THIS PHONE:",
                        color = palette.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Role switch buttons
                    val currentRole = profile?.myRole ?: "BOYFRIEND"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val isBoyfriend = currentRole == "BOYFRIEND"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isBoyfriend) Color(0xFF2979FF).copy(alpha = 0.30f) else Color.White.copy(alpha = 0.06f))
                                .border(1.dp, if (isBoyfriend) Color(0xFF2979FF) else Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                                .clickable { onSwitchRole("BOYFRIEND") }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "👦", fontSize = 20.sp)
                                Text(
                                    text = "Dayan (Boyfriend)",
                                    color = if (isBoyfriend) Color(0xFF82B1FF) else palette.textPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        val isGirlfriend = currentRole == "GIRLFRIEND"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isGirlfriend) Color(0xFFFF4081).copy(alpha = 0.30f) else Color.White.copy(alpha = 0.06f))
                                .border(1.dp, if (isGirlfriend) Color(0xFFFF4081) else Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                                .clickable { onSwitchRole("GIRLFRIEND") }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "👧", fontSize = 20.sp)
                                Text(
                                    text = "Girlfriend (Babe)",
                                    color = if (isGirlfriend) Color(0xFFFF80AB) else palette.textPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Shared Room Code Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.06f))
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Shared Couple Room Code:",
                                    color = palette.textSecondary,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = profile?.coupleSyncCode ?: "DAYAN-LOVE-2026",
                                    color = Color(0xFFFFD54F),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }

                            IconButton(
                                onClick = { showEditDialog = true },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Room Code",
                                    tint = palette.snapPrimaryYellow,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Instant Love Ping Action Row
                    Text(
                        text = "INSTANT LOVE PINGS:",
                        color = palette.textSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFF4081).copy(alpha = 0.20f))
                                .border(1.dp, Color(0xFFFF4081).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable { onLovePing("HEART") }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "❤️ Heart", color = Color(0xFFFF80AB), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFF4081).copy(alpha = 0.20f))
                                .border(1.dp, Color(0xFFFF4081).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable { onLovePing("KISS") }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "💋 Kiss", color = Color(0xFFFF80AB), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFF4081).copy(alpha = 0.20f))
                                .border(1.dp, Color(0xFFFF4081).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable { onLovePing("HUG") }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🫂 Hug", color = Color(0xFFFF80AB), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "💌 Both devices text in sub-second real-time with live typing, ephemeral deletes, and read receipts!",
                        color = palette.textTertiary,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Google Account & Google Messages 1-on-1 SMS Integration
            GoogleIntegrationCard(
                googleUser = googleUser,
                friendName = profile?.name ?: "My Babe 💖",
                friendPhone = profile?.phoneNumber ?: "+15551234567",
                onSignInDemo = onSignInGoogleDemo,
                onSignOut = onSignOutGoogle,
                onOpenGoogleMessages = onOpenGoogleMessages,
                onUpdateFriendPhone = { newPhone ->
                    val streak = profile?.streakCount ?: 142
                    onUpdateProfile(
                        profile?.name ?: "Alex",
                        profile?.handle ?: "alex.snap",
                        profile?.avatarEmoji ?: "👻",
                        streak,
                        newPhone
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Section 1: Dynamic Time of Day Liquid Glass Themes
            Text(
                text = "DYNAMIC LIQUID GLASS (TIME OF DAY)",
                color = palette.textSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Auto Chip
                val isAuto = profile?.timeOfDayTheme == "AUTO"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isAuto) palette.snapPrimaryYellow.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f))
                        .border(1.dp, if (isAuto) palette.snapPrimaryYellow else Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        .clickable { onTimeOfDaySelected("AUTO") }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔄", fontSize = 18.sp)
                        Text(
                            text = "Auto Clock",
                            color = if (isAuto) palette.snapPrimaryYellow else palette.textPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Force Dawn
                val isDawn = profile?.timeOfDayTheme == "DAWN"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDawn) Color(0xFFFF9E7D).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.08f))
                        .border(1.dp, if (isDawn) Color(0xFFFF9E7D) else Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        .clickable { onTimeOfDaySelected("DAWN") }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🌅", fontSize = 18.sp)
                        Text(
                            text = "Dawn",
                            color = palette.textPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Force Day
                val isDay = profile?.timeOfDayTheme == "DAY"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDay) Color(0xFFFFFC00).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.08f))
                        .border(1.dp, if (isDay) Color(0xFFFFFC00) else Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        .clickable { onTimeOfDaySelected("DAY") }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "☀️", fontSize = 18.sp)
                        Text(
                            text = "Day",
                            color = palette.textPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Force Sunset
                val isSunset = profile?.timeOfDayTheme == "SUNSET"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSunset) Color(0xFFFF2A85).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.08f))
                        .border(1.dp, if (isSunset) Color(0xFFFF2A85) else Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        .clickable { onTimeOfDaySelected("SUNSET") }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🌆", fontSize = 18.sp)
                        Text(
                            text = "Sunset",
                            color = palette.textPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Force Night
                val isNight = profile?.timeOfDayTheme == "NIGHT"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isNight) Color(0xFF00FFC2).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f))
                        .border(1.dp, if (isNight) Color(0xFF00FFC2) else Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        .clickable { onTimeOfDaySelected("NIGHT") }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🌙", fontSize = 18.sp)
                        Text(
                            text = "Night",
                            color = palette.textPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 2: Ephemeral & Privacy Controls
            Text(
                text = "EPHEMERAL PRIVACY CONTROLS",
                color = palette.textSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Option 1: Keep in Chat
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (ephemeralMode == "KEEP") palette.snapPrimaryYellow.copy(alpha = 0.20f)
                        else Color.White.copy(alpha = 0.06f)
                    )
                    .border(
                        1.dp,
                        if (ephemeralMode == "KEEP") palette.snapPrimaryYellow else Color.White.copy(alpha = 0.12f),
                        RoundedCornerShape(18.dp)
                    )
                    .clickable { onEphemeralModeSelected("KEEP") }
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = palette.snapPrimaryYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Keep Messages in Chat",
                            color = palette.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Messages remain saved in chat until manually burned.",
                            color = palette.textSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Option 2: Suddenly Delete After Read
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (ephemeralMode == "DELETE_AFTER_READ") palette.ephemeralBurnAccent.copy(alpha = 0.22f)
                        else Color.White.copy(alpha = 0.06f)
                    )
                    .border(
                        1.dp,
                        if (ephemeralMode == "DELETE_AFTER_READ") palette.ephemeralBurnAccent else Color.White.copy(alpha = 0.12f),
                        RoundedCornerShape(18.dp)
                    )
                    .clickable { onEphemeralModeSelected("DELETE_AFTER_READ") }
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = palette.ephemeralBurnAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Suddenly Delete After Read",
                            color = palette.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ephemeral burn: message dissolves right after viewing unless tapped to save.",
                            color = palette.textSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Haptic Feedback Demo Action
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(18.dp))
                    .clickable { onTestHapticFeedback() }
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = "Haptics",
                            tint = palette.snapPrimaryYellow,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Test Message Received Haptics",
                                color = palette.textPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Simulates the incoming Snapchat haptic buzz",
                                color = palette.textTertiary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.snapPrimaryYellow)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Vibrate",
                            color = palette.onAccentText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Clear Unsaved Messages Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.Red.copy(alpha = 0.12f))
                    .border(1.dp, Color.Red.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
                    .clickable { onClearUnsavedMessages() }
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear Chat",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Burn All Unsaved Messages",
                        color = Color(0xFFFF5252),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // E2E Security Fingerprint
            Text(
                text = "ENCRYPTED SESSION FINGERPRINT: ${profile?.encryptionKeyFingerprint ?: "48A2-9E71-F03B-CC89"}",
                color = palette.textTertiary,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
