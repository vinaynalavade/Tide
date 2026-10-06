package com.tide.app.core.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tide.app.core.theme.TideTheme

@Composable
fun TideCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = TideTheme.colors.cardBackground,
    borderColor: Color = TideTheme.colors.cardBorder,
    borderWidth: Dp = 1.dp,
    elevation: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .clip(shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = shape,
        color = backgroundColor,
        border = BorderStroke(borderWidth, borderColor),
        shadowElevation = elevation
    ) {
        Box(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun TideGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    TideCard(
        modifier = modifier,
        shape = shape,
        backgroundColor = TideTheme.colors.glassBackground,
        borderColor = TideTheme.colors.glassBorder,
        borderWidth = 1.dp,
        elevation = 2.dp,
        onClick = onClick,
        content = content
    )
}
