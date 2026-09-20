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
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GlassTheme

/**
 * Reusable Liquid Glass Container with specular reflections and frosted depth.
 */
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = GlassTheme.palette.glassCardBackground,
    borderColor: Color = GlassTheme.palette.glassCardBorder,
    borderWidth: Dp = 1.dp,
    elevation: Dp = 0.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .then(
                if (elevation > 0.dp) Modifier.shadow(elevation, shape)
                else Modifier
            )
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = borderWidth,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor.copy(alpha = 0.7f),
                        borderColor.copy(alpha = 0.2f),
                        borderColor.copy(alpha = 0.5f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                ),
                shape = shape
            ),
        content = content
    )
}

/**
 * Glowing Ambient Liquid Glass Pill with animated shimmer.
 */
@Composable
fun ShimmeringGlassPill(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(32.dp),
    accentColor: Color = GlassTheme.palette.snapPrimaryYellow,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glass_shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.White.copy(alpha = 0.10f))
            .border(
                width = 1.dp,
                color = accentColor.copy(alpha = shimmerAlpha),
                shape = shape
            ),
        content = content
    )
}

/**
 * Evanescent Particle Burn Container for Ephemeral Messages.
 */
@Composable
fun EphemeralVanishBox(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(tween(250)) + scaleIn(tween(250), initialScale = 0.85f),
        exit = fadeOut(tween(400, easing = FastOutSlowInEasing)) + scaleOut(
            tween(400, easing = FastOutSlowInEasing),
            targetScale = 0.6f
        )
    ) {
        content()
    }
}
