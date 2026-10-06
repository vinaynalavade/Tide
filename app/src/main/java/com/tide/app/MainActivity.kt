package com.tide.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tide.app.core.theme.TidePrimary
import com.tide.app.core.theme.TideTheme
import com.tide.app.ui.navigation.Screen
import com.tide.app.ui.navigation.TideNavHost
import com.tide.app.ui.settings.SettingsViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val app = application as TideApplication
        val repository = app.repository
        val inAppUpdateManager = app.inAppUpdateManager

        // Trigger in-app update check in background
        inAppUpdateManager.checkForUpdates()

        setContent {
            val settingsViewModel = remember { SettingsViewModel(repository, inAppUpdateManager) }
            val settingsState by settingsViewModel.uiState.collectAsState()

            val isSystemDark = isSystemInDarkTheme()
            val isDark = when (settingsState.userSettings.themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemDark
            }

            TideTheme(darkTheme = isDark) {
                if (settingsState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TidePrimary)
                    }
                } else {
                    val startDest = if (settingsState.userSettings.onboardingCompleted && settingsState.addiction != null) {
                        Screen.Main.route
                    } else {
                        Screen.Onboarding.route
                    }

                    TideNavHost(
                        repository = repository,
                        settingsViewModel = settingsViewModel,
                        startDestination = startDest
                    )
                }
            }
        }
    }
}
