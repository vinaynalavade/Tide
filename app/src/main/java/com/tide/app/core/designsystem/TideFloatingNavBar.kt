package com.tide.app.core.designsystem

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tide.app.core.theme.TidePrimary
import com.tide.app.core.theme.TidePrimaryLuminous
import com.tide.app.core.theme.TideTheme

enum class TideNavTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Outlined.Home),
    INSIGHTS("Insights", Icons.Outlined.Insights),
    HISTORY("History", Icons.Outlined.CalendarMonth),
    GOALS("Goals", Icons.Outlined.Flag),
    SETTINGS("Settings", Icons.Outlined.Settings)
}

@Composable
fun TideFloatingNavBar(
    selectedTab: TideNavTab,
    onTabSelected: (TideNavTab) -> Unit,
    onQuickLogClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = TideTheme.colors.glassBackground,
            border = BorderStroke(1.dp, TideTheme.colors.glassBorder),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Home
                NavTabItem(
                    tab = TideNavTab.HOME,
                    isSelected = selectedTab == TideNavTab.HOME,
                    onClick = { onTabSelected(TideNavTab.HOME) }
                )

                // Insights
                NavTabItem(
                    tab = TideNavTab.INSIGHTS,
                    isSelected = selectedTab == TideNavTab.INSIGHTS,
                    onClick = { onTabSelected(TideNavTab.INSIGHTS) }
                )

                // Central Prominent "+ Log" Action
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(TidePrimary, TidePrimaryLuminous)
                            )
                        )
                        .clickable(onClick = onQuickLogClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Log Entry",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // History
                NavTabItem(
                    tab = TideNavTab.HISTORY,
                    isSelected = selectedTab == TideNavTab.HISTORY,
                    onClick = { onTabSelected(TideNavTab.HISTORY) }
                )

                // Goals / Settings
                NavTabItem(
                    tab = TideNavTab.GOALS,
                    isSelected = selectedTab == TideNavTab.GOALS,
                    onClick = { onTabSelected(TideNavTab.GOALS) }
                )
            }
        }
    }
}

@Composable
private fun NavTabItem(
    tab: TideNavTab,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val tint by animateColorAsState(
        targetValue = if (isSelected) TidePrimaryLuminous else TideTheme.colors.textSecondary,
        animationSpec = tween(200),
        label = "nav_tint"
    )

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = tab.icon,
            contentDescription = tab.title,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = tab.title,
            fontSize = 10.sp,
            style = MaterialTheme.typography.labelSmall,
            color = tint
        )
    }
}
