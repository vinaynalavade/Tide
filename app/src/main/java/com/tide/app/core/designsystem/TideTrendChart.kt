package com.tide.app.core.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tide.app.core.theme.TidePrimary
import com.tide.app.core.theme.TideTheme
import kotlin.math.max

data class ChartBarData(
    val label: String,
    val value: Double
)

@Composable
fun TideTrendChart(
    data: List<ChartBarData>,
    modifier: Modifier = Modifier,
    barColor: Color = TidePrimary,
    unitSuffix: String = ""
) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(140.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No trend data yet",
                style = MaterialTheme.typography.bodySmall,
                color = TideTheme.colors.textTertiary
            )
        }
        return
    }

    val maxValue = max(1.0, data.maxOf { it.value })

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            val barCount = data.size
            val availableWidth = size.width
            val totalSpacing = availableWidth * 0.25f
            val spacing = totalSpacing / (barCount + 1)
            val barWidth = (availableWidth - totalSpacing) / barCount

            data.forEachIndexed { index, item ->
                val barHeight = ((item.value / maxValue) * (size.height - 20f)).toFloat()
                val left = spacing + index * (barWidth + spacing)
                val top = size.height - barHeight

                // Draw bar background slot
                drawRoundRect(
                    color = barColor.copy(alpha = 0.12f),
                    topLeft = Offset(left, 0f),
                    size = Size(barWidth, size.height),
                    cornerRadius = CornerRadius(8f, 8f)
                )

                // Draw active bar
                if (barHeight > 0) {
                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(left, top),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val step = max(1, data.size / 7)
            data.forEachIndexed { index, item ->
                if (index % step == 0 || index == data.lastIndex) {
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        color = TideTheme.colors.textTertiary
                    )
                }
            }
        }
    }
}
