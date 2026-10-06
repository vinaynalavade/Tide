package com.tide.app.ui.insights

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tide.app.core.designsystem.MetricCard
import com.tide.app.core.designsystem.TideCard
import com.tide.app.core.designsystem.TideTopBar
import com.tide.app.core.designsystem.TideTrendChart
import com.tide.app.core.theme.TidePrimary
import com.tide.app.core.theme.TidePrimaryLuminous
import com.tide.app.core.theme.TideSlip
import com.tide.app.core.theme.TideSuccess
import com.tide.app.core.theme.TideTheme

@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = TidePrimary)
        }
        return
    }

    val addiction = state.addiction ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
    ) {
        TideTopBar(
            title = "Insights",
            subtitle = "Patterns & behavioral trends"
        )

        // Progress Message Card
        ProgressCard(
            progress = state.progressInsight,
            unit = addiction.unit
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Timeframe Selector & Consumption Trend
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Consumption Trend",
                    style = MaterialTheme.typography.titleMedium,
                    color = TideTheme.colors.textPrimary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TimeframeChip(
                        label = "7 Days",
                        isSelected = state.timeframe == TrendTimeframe.LAST_7_DAYS,
                        onClick = { viewModel.setTimeframe(TrendTimeframe.LAST_7_DAYS) }
                    )
                    TimeframeChip(
                        label = "30 Days",
                        isSelected = state.timeframe == TrendTimeframe.LAST_30_DAYS,
                        onClick = { viewModel.setTimeframe(TrendTimeframe.LAST_30_DAYS) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TideCard {
                TideTrendChart(
                    data = state.consumptionChartData,
                    barColor = TidePrimaryLuminous,
                    unitSuffix = addiction.unit
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Financial Trends
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Financial Impact",
                style = MaterialTheme.typography.titleMedium,
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Money Saved",
                    value = "₹${state.money.lifetimeSaved.toInt()}",
                    subtitle = "Projected/yr: ₹${state.money.yearlyProjectedSaved.toInt()}",
                    icon = Icons.Default.LocalAtm,
                    accentColor = TideSuccess,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Money Spent",
                    value = "₹${state.money.lifetimeSpent.toInt()}",
                    subtitle = "7-day: ₹${state.money.last7DaysSpent.toInt()}",
                    icon = Icons.Default.LocalAtm,
                    accentColor = TideSlip,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Cravings Analysis
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Craving Urges",
                style = MaterialTheme.typography.titleMedium,
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            TideCard {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total Logged: ${state.cravingInsight.totalCravings}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TideTheme.colors.textPrimary
                            )
                            Text(
                                text = "Avg Intensity: ${state.cravingInsight.averageIntensity} / 10",
                                style = MaterialTheme.typography.bodySmall,
                                color = TideTheme.colors.textSecondary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${state.cravingInsight.resistedPercentage}%",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TideSuccess
                            )
                            Text(
                                text = "${state.cravingInsight.resistedCount} resisted",
                                style = MaterialTheme.typography.labelSmall,
                                color = TideTheme.colors.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { (state.cravingInsight.resistedPercentage / 100.0).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = TideSuccess,
                        trackColor = TideTheme.colors.cardBorder
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Triggers Breakdown (Section 25 & 26)
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Strongest Triggers",
                style = MaterialTheme.typography.titleMedium,
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (!state.progressInsight.hasSufficientData || state.triggersInsight.isEmpty()) {
                TideCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = TidePrimaryLuminous,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Keep logging to discover your strongest triggers.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TideTheme.colors.textSecondary
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.triggersInsight.forEach { trigger ->
                        TriggerRow(trigger = trigger)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressCard(
    progress: com.tide.app.core.calculation.ProgressInsight,
    unit: String
) {
    TideCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        backgroundColor = TidePrimary.copy(alpha = 0.08f),
        borderColor = TidePrimaryLuminous.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = TidePrimaryLuminous.copy(alpha = 0.2f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                        contentDescription = null,
                        tint = TidePrimaryLuminous,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = progress.message,
                    style = MaterialTheme.typography.titleSmall,
                    color = TideTheme.colors.textPrimary
                )
                if (progress.hasSufficientData) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Daily avg: ${progress.currentDailyAverage} vs baseline: ${progress.baselineDailyAmount} $unit",
                        style = MaterialTheme.typography.bodySmall,
                        color = TideTheme.colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun TriggerRow(
    trigger: com.tide.app.core.calculation.TriggerInsight
) {
    TideCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = trigger.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = TideTheme.colors.textPrimary
                )
                Text(
                    text = "${trigger.percentage.toInt()}% (${trigger.count})",
                    style = MaterialTheme.typography.labelMedium,
                    color = TideTheme.colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { (trigger.percentage / 100.0).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = TidePrimaryLuminous,
                trackColor = TideTheme.colors.cardBorder
            )
        }
    }
}

@Composable
private fun TimeframeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) TidePrimary.copy(alpha = 0.15f) else TideTheme.colors.cardBackground,
        border = BorderStroke(1.dp, if (isSelected) TidePrimaryLuminous else TideTheme.colors.cardBorder),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) TidePrimaryLuminous else TideTheme.colors.textSecondary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
