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
import com.example.data.FriendProfile
import com.example.ui.theme.GlassTheme
import com.example.ui.theme.TimeOfDay

@Composable
fun FriendProfileSheet(
    profile: FriendProfile?,
    currentTimeOfDay: TimeOfDay,
    onTimeOfDaySelected: (String) -> Unit,
    ephemeralMode: String,
    onEphemeralModeSelected: (String) -> Unit,
    onUpdateProfile: (name: String, handle: String, avatar: String, streak: Int) -> Unit,
    onTestHapticFeedback: () -> Unit,
    onClearUnsavedMessages: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = GlassTheme.palette
    var showEditDialog by remember { mutableStateOf(false) }

    var editName by remember { mutableStateOf(profile?.name ?: "Alex") }
    var editHandle by remember { mutableStateOf(profile?.handle ?: "alex.snap") }
    var editAvatar by remember { mutableStateOf(profile?.avatarEmoji ?: "👻") }
    var editStreak by remember { mutableStateOf((profile?.streakCount ?: 142).toString()) }

    val avatarOptions = listOf("👻", "⚡", "👑", "💅", "🔥", "🦄", "👽", "🐱", "🐶", "🥑")

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(text = "Edit Friend Profile", fontWeight = FontWeight.Bold)
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
                        value = editStreak,
                        onValueChange = { editStreak = it },
                        label = { Text("Streak Days") },
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
                        val streak = editStreak.toIntOrNull() ?: 142
                        onUpdateProfile(editName, editHandle, editAvatar, streak)
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
                        imageVector = Icons.Rounded.Shield,
                        contentDescription = "Encrypted Friend",
                        tint = palette.snapPrimaryYellow,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SOLO SNAP PROFILE",
                        color = palette.snapPrimaryYellow,
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

            // Main Friend Avatar Card
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
                            .border(2.dp, palette.snapPrimaryYellow, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile?.avatarEmoji ?: "👻",
                            fontSize = 46.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = profile?.name ?: "Alex",
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
                        text = "@${profile?.handle ?: "alex.snap"}",
                        color = palette.textSecondary,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Streak Flame Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(palette.snapPrimaryYellow.copy(alpha = 0.25f))
                            .border(1.dp, palette.snapPrimaryYellow, RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = palette.snapPrimaryYellow,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${profile?.streakCount ?: 142} Days Streak! 💛 Best Friends",
                                color = palette.snapPrimaryYellow,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

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
