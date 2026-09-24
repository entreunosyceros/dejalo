package com.dejalo.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dejalo.app.data.QuitRepository
import com.dejalo.app.ui.dashboard.DashboardScreen
import com.dejalo.app.ui.hubs.ProgressHubScreen
import com.dejalo.app.ui.hubs.SettingsHubScreen
import com.dejalo.app.ui.hubs.ToolsHubScreen
import com.dejalo.app.ui.theme.DejaloColors

@Composable
fun MainScaffold(
    repository: QuitRepository,
    rootNav: NavHostController,
    onRequestNotificationPermission: (() -> Unit)? = null
) {
    val tabNav = rememberNavController()
    val backStack by tabNav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        bottomBar = {
            NavigationBar(
                containerColor = DejaloColors.Cloud.copy(alpha = 0.96f),
                contentColor = DejaloColors.Navy
            ) {
                MainTab.entries.forEach { tab ->
                    val selected = currentRoute == tab.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            tabNav.navigate(tab.route) {
                                popUpTo(tabNav.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = when (tab) {
                                    MainTab.Home -> Icons.Default.Home
                                    MainTab.Progress -> Icons.Default.Timeline
                                    MainTab.Tools -> Icons.Default.Build
                                    MainTab.Settings -> Icons.Default.Settings
                                },
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DejaloColors.TealDeep,
                            selectedTextColor = DejaloColors.TealDeep,
                            indicatorColor = DejaloColors.Teal.copy(alpha = 0.22f),
                            unselectedIconColor = DejaloColors.InkMuted,
                            unselectedTextColor = DejaloColors.InkMuted
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = tabNav,
            startDestination = Routes.Home,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.Home) {
                DashboardScreen(
                    repository = repository,
                    onEmergency = { rootNav.navigate(Routes.Emergency) },
                    onNotNow = { rootNav.navigate(Routes.NotNow) },
                    onRelapse = { rootNav.navigate(Routes.Relapse) },
                    onRiskZones = { rootNav.navigate(Routes.RiskZones) },
                    onAnsiaHistory = { rootNav.navigate(Routes.AnsiaHistory) }
                )
            }
            composable(Routes.ProgressHub) {
                ProgressHubScreen(
                    onHealth = { rootNav.navigate(Routes.Health) },
                    onAchievements = { rootNav.navigate(Routes.Achievements) },
                    onLearnings = { rootNav.navigate(Routes.Learnings) },
                    onAnsiaHistory = { rootNav.navigate(Routes.AnsiaHistory) }
                )
            }
            composable(Routes.ToolsHub) {
                ToolsHubScreen(
                    onGames = { rootNav.navigate(Routes.Games) },
                    onRoutines = { rootNav.navigate(Routes.Routines) },
                    onRiskZones = { rootNav.navigate(Routes.RiskZones) },
                    onEmergency = { rootNav.navigate(Routes.Emergency) }
                )
            }
            composable(Routes.SettingsHub) {
                SettingsHubScreen(
                    onSettings = { rootNav.navigate(Routes.Settings) },
                    onAbout = { rootNav.navigate(Routes.About) },
                    onDocs = { rootNav.navigate(Routes.Docs) },
                    onBackup = { rootNav.navigate(Routes.Backup) },
                    onRequestNotifications = onRequestNotificationPermission
                )
            }
        }
    }
}
