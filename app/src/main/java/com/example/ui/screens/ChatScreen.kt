package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.ui.components.ChatComposer
import com.example.ui.components.ChatHeader
import com.example.ui.components.EmojiDrawer
import com.example.ui.components.FriendProfileSheet
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.MessageItem
import com.example.ui.theme.GlassTheme
import com.example.ui.theme.LocalLiquidGlassPalette
import com.example.ui.theme.TimeOfDay
import com.example.ui.theme.getPaletteForTime
import com.example.viewmodel.ChatViewModel

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messages.collectAsState()
    val friendProfile by viewModel.friendProfile.collectAsState()
    val isFriendTyping by viewModel.isFriendTyping.collectAsState()
    val currentEphemeralMode by viewModel.currentEphemeralMode.collectAsState()
    val manualTimeOfDay by viewModel.manualTimeOfDay.collectAsState()

    // Determine current TimeOfDay based on manual override or real clock
    val activeTimeOfDay = remember(manualTimeOfDay, friendProfile?.timeOfDayTheme) {
        when {
            manualTimeOfDay != null -> manualTimeOfDay!!
            friendProfile?.timeOfDayTheme == "DAWN" -> TimeOfDay.DAWN
            friendProfile?.timeOfDayTheme == "DAY" -> TimeOfDay.DAY
            friendProfile?.timeOfDayTheme == "SUNSET" -> TimeOfDay.SUNSET
            friendProfile?.timeOfDayTheme == "NIGHT" -> TimeOfDay.NIGHT
            else -> TimeOfDay.fromCurrentHour()
        }
    }

    val activePalette = remember(activeTimeOfDay) {
        getPaletteForTime(activeTimeOfDay)
    }

    var inputText by remember { mutableStateOf("") }
    var isEmojiDrawerOpen by remember { mutableStateOf(false) }
    var isProfileSheetOpen by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    CompositionLocalProvider(LocalLiquidGlassPalette provides activePalette) {
        val palette = GlassTheme.palette

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(palette.backgroundBrush)
                .testTag("chat_screen_root")
        ) {
            // Ambient Liquid Glass Glow Layer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(palette.ambientGlowBrush)
            )

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Liquid Glass Header
                ChatHeader(
                    profile = friendProfile,
                    isFriendTyping = isFriendTyping,
                    currentTimeOfDay = activeTimeOfDay,
                    ephemeralMode = currentEphemeralMode,
                    onProfileClick = {
                        isProfileSheetOpen = true
                        isEmojiDrawerOpen = false
                    },
                    onEphemeralToggleClick = {
                        val next = if (currentEphemeralMode == "KEEP") "DELETE_AFTER_READ" else "KEEP"
                        viewModel.setEphemeralMode(next)
                    },
                    onTimeOfDayToggleClick = {
                        // Cycle through time of days
                        val nextTime = when (activeTimeOfDay) {
                            TimeOfDay.DAWN -> "DAY"
                            TimeOfDay.DAY -> "SUNSET"
                            TimeOfDay.SUNSET -> "NIGHT"
                            TimeOfDay.NIGHT -> "DAWN"
                        }
                        viewModel.setTimeOfDayMode(nextTime)
                    }
                )

                // Message List Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (messages.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LiquidGlassCard(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .padding(16.dp),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "👻",
                                        fontSize = 44.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Direct Liquid Chat with ${friendProfile?.name ?: "Alex"}",
                                        color = palette.textPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Tap messages to save them in chat. Send funny emoji stickers or activate sudden ephemeral burn!",
                                        color = palette.textSecondary,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("messages_lazy_column"),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(
                                items = messages,
                                key = { it.id }
                            ) { message ->
                                MessageItem(
                                    message = message,
                                    onToggleSave = { viewModel.toggleSaveMessage(it) },
                                    onBurnImmediately = { viewModel.burnMessageImmediately(it) },
                                    onMarkRead = { viewModel.markMessageRead(it) }
                                )
                            }
                        }
                    }
                }

                // Chat Composer
                ChatComposer(
                    text = inputText,
                    onTextChanged = { inputText = it },
                    onSendMessage = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    },
                    ephemeralMode = currentEphemeralMode,
                    onToggleEphemeral = {
                        val next = if (currentEphemeralMode == "KEEP") "DELETE_AFTER_READ" else "KEEP"
                        viewModel.setEphemeralMode(next)
                    },
                    isEmojiDrawerOpen = isEmojiDrawerOpen,
                    onToggleEmojiDrawer = {
                        isEmojiDrawerOpen = !isEmojiDrawerOpen
                        if (isEmojiDrawerOpen) {
                            isProfileSheetOpen = false
                        }
                    },
                    onSimulateIncomingMessage = {
                        viewModel.triggerInstantFriendMessage()
                    }
                )

                // Funny Emoji Drawer (Expandable)
                AnimatedVisibility(
                    visible = isEmojiDrawerOpen,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    EmojiDrawer(
                        onSendSticker = { stickerEmoji ->
                            viewModel.sendMessage(
                                text = "",
                                mediaType = "STICKER",
                                emojiSticker = stickerEmoji
                            )
                            viewModel.hapticManager.vibrateTick()
                        },
                        onInsertEmoji = { emoji ->
                            inputText += emoji
                            viewModel.hapticManager.vibrateTick()
                        },
                        onClose = { isEmojiDrawerOpen = false }
                    )
                }
            }

            // Friend Profile & Liquid Settings Sheet Overlay
            AnimatedVisibility(
                visible = isProfileSheetOpen,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                FriendProfileSheet(
                    profile = friendProfile,
                    currentTimeOfDay = activeTimeOfDay,
                    onTimeOfDaySelected = { mode ->
                        viewModel.setTimeOfDayMode(mode)
                    },
                    ephemeralMode = currentEphemeralMode,
                    onEphemeralModeSelected = { mode ->
                        viewModel.setEphemeralMode(mode)
                    },
                    onUpdateProfile = { name, handle, avatar, streak ->
                        viewModel.updateFriendProfile(name, handle, avatar, streak)
                    },
                    onTestHapticFeedback = {
                        viewModel.hapticManager.vibrateMessageReceived()
                    },
                    onClearUnsavedMessages = {
                        viewModel.clearUnsavedMessages()
                        isProfileSheetOpen = false
                    },
                    onClose = { isProfileSheetOpen = false }
                )
            }
        }
    }
}
