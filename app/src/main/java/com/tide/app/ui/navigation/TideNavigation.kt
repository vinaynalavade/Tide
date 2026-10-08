package com.tide.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tide.app.core.designsystem.TideFloatingNavBar
import com.tide.app.core.designsystem.TideNavTab
import com.tide.app.core.theme.TideTheme
import com.tide.app.data.repository.TideRepository
import com.tide.app.ui.craving.CravingModeScreen
import com.tide.app.ui.craving.CravingModeViewModel
import com.tide.app.ui.goals.GoalsScreen
import com.tide.app.ui.goals.GoalsViewModel
import com.tide.app.ui.history.HistoryScreen
import com.tide.app.ui.history.HistoryViewModel
import com.tide.app.ui.home.HomeScreen
import com.tide.app.ui.home.HomeViewModel
import com.tide.app.ui.insights.InsightsScreen
import com.tide.app.ui.insights.InsightsViewModel
import com.tide.app.ui.onboarding.OnboardingScreen
import com.tide.app.ui.onboarding.OnboardingViewModel
import com.tide.app.ui.settings.SettingsScreen
import com.tide.app.ui.settings.SettingsViewModel

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Main : Screen("main")
    object CravingMode : Screen("craving_mode")
}

@Composable
fun TideNavHost(
    navController: NavHostController = rememberNavController(),
    repository: TideRepository,
    settingsViewModel: SettingsViewModel,
    startDestination: String = Screen.Onboarding.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier
            .fillMaxSize()
            .background(TideTheme.colors.cardBackground)
    ) {
        composable(Screen.Onboarding.route) {
            val onboardingViewModel = remember { OnboardingViewModel(repository) }
            OnboardingScreen(
                viewModel = onboardingViewModel,
                onOnboardingFinished = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainContainerScreen(
                repository = repository,
                settingsViewModel = settingsViewModel,
                onNavigateToCravingMode = {
                    navController.navigate(Screen.CravingMode.route)
                },
                onResetToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.CravingMode.route) {
            val cravingViewModel = remember { CravingModeViewModel(repository) }
            CravingModeScreen(
                viewModel = cravingViewModel,
                onClose = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun MainContainerScreen(
    repository: TideRepository,
    settingsViewModel: SettingsViewModel,
    onNavigateToCravingMode: () -> Unit,
    onResetToOnboarding: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var selectedTab by remember { mutableStateOf(TideNavTab.HOME) }
    var showQuickLogSheet by remember { mutableStateOf(false) }

    val homeViewModel = remember { HomeViewModel(repository, context) }
    val insightsViewModel = remember { InsightsViewModel(repository) }
    val historyViewModel = remember { HistoryViewModel(repository) }
    val goalsViewModel = remember { GoalsViewModel(repository) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (selectedTab) {
            TideNavTab.HOME -> {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToCravingMode = onNavigateToCravingMode,
                    showQuickLogSheet = showQuickLogSheet,
                    onDismissQuickLogSheet = { showQuickLogSheet = false }
                )
            }
            TideNavTab.INSIGHTS -> {
                InsightsScreen(viewModel = insightsViewModel)
            }
            TideNavTab.HISTORY -> {
                HistoryScreen(viewModel = historyViewModel)
            }
            TideNavTab.GOALS -> {
                GoalsScreen(
                    viewModel = goalsViewModel,
                    onNavigateToSettings = { selectedTab = TideNavTab.SETTINGS }
                )
            }
            TideNavTab.SETTINGS -> {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onResetToOnboarding = onResetToOnboarding
                )
            }
        }

        // Floating Glass Navigation Bar
        TideFloatingNavBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            onQuickLogClick = {
                // If not on Home, navigate to Home and open sheet
                if (selectedTab != TideNavTab.HOME) {
                    selectedTab = TideNavTab.HOME
                }
                showQuickLogSheet = true
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
