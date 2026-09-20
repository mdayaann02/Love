package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassTheme

@Composable
fun EmojiDrawer(
    onSendSticker: (String) -> Unit,
    onInsertEmoji: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = GlassTheme.palette
    var selectedPackId by remember { mutableStateOf(FunnyEmojiPacks.packs[0].id) }
    var sendAsBigSticker by remember { mutableStateOf(true) }

    val currentPack = remember(selectedPackId) {
        FunnyEmojiPacks.packs.find { it.id == selectedPackId } ?: FunnyEmojiPacks.packs[0]
    }

    LiquidGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            .testTag("emoji_drawer"),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        backgroundColor = palette.glassHeaderBackground,
        borderColor = palette.glassHeaderBorder,
        borderWidth = 1.2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
        ) {
            // Header Row: Pack title, mode toggle, close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${currentPack.iconEmoji} ${currentPack.title}",
                        color = palette.textPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(palette.snapPrimaryYellow.copy(alpha = 0.25f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = currentPack.badge,
                            color = palette.snapPrimaryYellow,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Send As Sticker vs Insert Text mode chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (sendAsBigSticker) palette.snapPrimaryYellow.copy(alpha = 0.25f)
                                else Color.White.copy(alpha = 0.15f)
                            )
                            .border(
                                1.dp,
                                if (sendAsBigSticker) palette.snapPrimaryYellow else Color.White.copy(alpha = 0.3f),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { sendAsBigSticker = !sendAsBigSticker }
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (sendAsBigSticker) "⚡ Giant Sticker" else "✏️ Text Insert",
                            color = if (sendAsBigSticker) palette.snapPrimaryYellow else palette.textSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Emoji Drawer",
                            tint = palette.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Category Tab Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(FunnyEmojiPacks.packs) { pack ->
                    val isSelected = pack.id == selectedPackId
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) palette.snapPrimaryYellow
                                else Color.White.copy(alpha = 0.10f)
                            )
                            .border(
                                1.dp,
                                if (isSelected) palette.snapPrimaryYellow
                                else Color.White.copy(alpha = 0.20f),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedPackId = pack.id }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("emoji_pack_tab_${pack.id}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = pack.iconEmoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = pack.title,
                                color = if (isSelected) palette.onAccentText else palette.textPrimary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Emoji Grid
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 46.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(currentPack.emojis) { emoji ->
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                            .clickable {
                                if (sendAsBigSticker) {
                                    onSendSticker(emoji)
                                } else {
                                    onInsertEmoji(emoji)
                                }
                            }
                            .testTag("emoji_item_$emoji"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }
    }
}
