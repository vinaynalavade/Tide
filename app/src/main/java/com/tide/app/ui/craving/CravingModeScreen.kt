package com.tide.app.ui.craving

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tide.app.core.designsystem.BreathingWaveView
import com.tide.app.core.designsystem.TideCard
import com.tide.app.core.designsystem.TidePrimaryButton
import com.tide.app.core.designsystem.TideSecondaryButton
import com.tide.app.core.theme.TidePrimaryLuminous
import com.tide.app.core.theme.TideSuccess
import com.tide.app.core.theme.TideTheme

@Composable
fun CravingModeScreen(
    viewModel: CravingModeViewModel,
    onClose: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        // Close button in top-right
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(44.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Exit",
                tint = TideTheme.colors.textSecondary
            )
        }

        AnimatedContent(
            targetState = state.stage,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "craving_stage",
            modifier = Modifier.fillMaxSize()
        ) { stage ->
            when (stage) {
                CravingStage.BREATHING -> {
                    BreathingPhase(
                        onCompleted = { viewModel.onBreathingCompleted() },
                        onSkip = { viewModel.onBreathingCompleted() }
                    )
                }
                CravingStage.CHECK_IN -> {
                    PostCheckInPhase(
                        onSelect = { viewModel.submitFeedback(it) }
                    )
                }
                CravingStage.COMPLETED_BETTER -> {
                    BetterResultPhase(
                        onFinish = onClose
                    )
                }
                CravingStage.SUPPORT_OPTIONS -> {
                    SupportOptionsPhase(
                        onAnotherMinute = { viewModel.restartBreathing() },
                        onDone = onClose
                    )
                }
            }
        }
    }
}

@Composable
private fun BreathingPhase(
    onCompleted: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Text(
                text = "Pause.",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = 2.sp
                ),
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Cravings rise and fall like tides.\nLet the breath anchor you.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = TideTheme.colors.textSecondary
            )
        }

        BreathingWaveView(
            totalSecondsTarget = 60,
            onCompleted = onCompleted
        )

        Column(modifier = Modifier.padding(bottom = 20.dp)) {
            TideSecondaryButton(
                text = "I'm ready now",
                onClick = onSkip
            )
        }
    }
}

@Composable
private fun PostCheckInPhase(
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "How are you feeling now?",
            style = MaterialTheme.typography.headlineMedium,
            color = TideTheme.colors.textPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Notice any shift in intensity.",
            style = MaterialTheme.typography.bodyMedium,
            color = TideTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(36.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FeedbackOptionCard(
                label = "Better",
                subtitle = "Urge softened",
                accentColor = TideSuccess,
                modifier = Modifier.weight(1f),
                onClick = { onSelect("Better") }
            )

            FeedbackOptionCard(
                label = "Same",
                subtitle = "Still present",
                accentColor = TideTheme.colors.warning,
                modifier = Modifier.weight(1f),
                onClick = { onSelect("Same") }
            )

            FeedbackOptionCard(
                label = "Worse",
                subtitle = "Intense urge",
                accentColor = TidePrimaryLuminous,
                modifier = Modifier.weight(1f),
                onClick = { onSelect("Worse") }
            )
        }
    }
}

@Composable
private fun FeedbackOptionCard(
    label: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = TideTheme.colors.cardBackground,
        border = BorderStroke(1.dp, TideTheme.colors.cardBorder),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = TideTheme.colors.textSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun BetterResultPhase(
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = CircleShape,
                color = TideSuccess.copy(alpha = 0.15f),
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = TideSuccess,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "You rode the wave.",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = TideTheme.colors.textPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Each craving resisted rewires the pattern. Stay with the tide.",
                style = MaterialTheme.typography.bodyLarge,
                color = TideTheme.colors.textSecondary,
                textAlign = TextAlign.Center
            )
        }

        Column(modifier = Modifier.padding(bottom = 20.dp)) {
            TidePrimaryButton(
                text = "Return to Dashboard",
                onClick = onFinish
            )
        }
    }
}

@Composable
private fun SupportOptionsPhase(
    onAnotherMinute: () -> Unit,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.padding(top = 24.dp)) {
            Text(
                text = "Still with you.",
                style = MaterialTheme.typography.headlineLarge,
                color = TideTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Give your brain a small interruption. Choose a grounding action:",
                style = MaterialTheme.typography.bodyMedium,
                color = TideTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            SupportActionItem(
                title = "Another minute of breath",
                description = "Extend your pause for 60 seconds",
                icon = Icons.Default.Refresh,
                onClick = onAnotherMinute
            )

            Spacer(modifier = Modifier.height(12.dp))

            SupportActionItem(
                title = "Take a short walk",
                description = "Change your physical environment",
                icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                onClick = onDone
            )

            Spacer(modifier = Modifier.height(12.dp))

            SupportActionItem(
                title = "Drink a glass of water",
                description = "Physical sensory reset",
                icon = Icons.Default.SelfImprovement,
                onClick = onDone
            )

            Spacer(modifier = Modifier.height(12.dp))

            SupportActionItem(
                title = "Reach out to someone",
                description = "A friend or supportive contact",
                icon = Icons.Default.Phone,
                onClick = onDone
            )
        }

        Column(modifier = Modifier.padding(bottom = 20.dp)) {
            TideSecondaryButton(
                text = "Done for now",
                onClick = onDone
            )
        }
    }
}

@Composable
private fun SupportActionItem(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    TideCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = TidePrimaryLuminous.copy(alpha = 0.15f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = TidePrimaryLuminous,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleSmall, color = TideTheme.colors.textPrimary)
                Text(description, style = MaterialTheme.typography.bodySmall, color = TideTheme.colors.textSecondary)
            }
        }
    }
}
