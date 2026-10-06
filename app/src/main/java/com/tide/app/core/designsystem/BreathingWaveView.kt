package com.tide.app.core.designsystem

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tide.app.core.theme.TidePrimary
import com.tide.app.core.theme.TidePrimaryLuminous
import com.tide.app.core.theme.TideWave
import kotlinx.coroutines.delay

enum class BreathPhase(val label: String, val durationMs: Int) {
    INHALE("Inhale", 4000),
    HOLD("Hold", 4000),
    EXHALE("Exhale", 4000),
    REST("Rest", 2000)
}

@Composable
fun BreathingWaveView(
    modifier: Modifier = Modifier,
    totalSecondsTarget: Int = 60,
    onCompleted: () -> Unit = {}
) {
    var currentPhase by remember { mutableStateOf(BreathPhase.INHALE) }
    var secondsElapsed by remember { mutableIntStateOf(0) }
    val scaleAnim = remember { Animatable(0.4f) }

    // Timer tracking
    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        while (true) {
            val elapsed = ((System.currentTimeMillis() - startTime) / 1000).toInt()
            secondsElapsed = elapsed
            if (elapsed >= totalSecondsTarget) {
                onCompleted()
                break
            }
            delay(500)
        }
    }

    // Breathing animation cycle
    LaunchedEffect(Unit) {
        while (true) {
            // INHALE
            currentPhase = BreathPhase.INHALE
            scaleAnim.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(BreathPhase.INHALE.durationMs, easing = LinearEasing)
            )

            // HOLD
            currentPhase = BreathPhase.HOLD
            delay(BreathPhase.HOLD.durationMs.toLong())

            // EXHALE
            currentPhase = BreathPhase.EXHALE
            scaleAnim.animateTo(
                targetValue = 0.4f,
                animationSpec = tween(BreathPhase.EXHALE.durationMs, easing = LinearEasing)
            )

            // REST
            currentPhase = BreathPhase.REST
            delay(BreathPhase.REST.durationMs.toLong())
        }
    }

    val remaining = (totalSecondsTarget - secondsElapsed).coerceAtLeast(0)

    Box(
        modifier = modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        val currentScale = scaleAnim.value

        Canvas(modifier = Modifier.size(260.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = size.width / 2f

            // Outer subtle wave ring
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        TideWave.copy(alpha = 0.08f * currentScale),
                        Color.Transparent
                    ),
                    center = center,
                    radius = maxRadius * 1.1f
                ),
                radius = maxRadius * currentScale,
                center = center
            )

            // Mid tidal circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        TidePrimaryLuminous.copy(alpha = 0.25f),
                        TidePrimary.copy(alpha = 0.1f)
                    ),
                    center = center,
                    radius = maxRadius * 0.85f
                ),
                radius = maxRadius * 0.85f * currentScale,
                center = center
            )

            // Core calm circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        TidePrimaryLuminous.copy(alpha = 0.6f),
                        TidePrimary.copy(alpha = 0.35f)
                    ),
                    center = center,
                    radius = maxRadius * 0.55f
                ),
                radius = maxRadius * 0.55f * currentScale,
                center = center
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentPhase.label.uppercase(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = 4.sp
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${remaining}s",
                style = MaterialTheme.typography.bodyMedium,
                color = TidePrimaryLuminous.copy(alpha = 0.8f)
            )
        }
    }
}
