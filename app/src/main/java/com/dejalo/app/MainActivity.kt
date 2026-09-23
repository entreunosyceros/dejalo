package com.dejalo.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dejalo.app.ui.about.AboutScreen
import com.dejalo.app.ui.achievements.AchievementsScreen
import com.dejalo.app.ui.ansia.AnsiaHistoryScreen
import com.dejalo.app.ui.dashboard.DashboardScreen
import com.dejalo.app.ui.docs.DocsScreen
import com.dejalo.app.ui.emergency.EmergencyScreen
import com.dejalo.app.ui.games.GamesHubScreen
import com.dejalo.app.ui.health.HealthScreen
import com.dejalo.app.ui.learnings.LearningsScreen
import com.dejalo.app.ui.navigation.Routes
import com.dejalo.app.ui.notnow.NotNowScreen
import com.dejalo.app.ui.onboarding.OnboardingScreen
import com.dejalo.app.ui.relapse.RelapseScreen
import com.dejalo.app.ui.risk.RiskZonesScreen
import com.dejalo.app.ui.routines.RoutinesScreen
import com.dejalo.app.ui.settings.SettingsScreen
import com.dejalo.app.ui.theme.DejaloColors
import com.dejalo.app.ui.theme.DejaloTheme
import com.dejalo.app.widget.WidgetUpdater
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {

    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* optional */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationsIfNeeded()

        val openEmergency = intent?.action == "com.dejalo.app.OPEN_EMERGENCY"
        val repository = application.dejaloRepository

        setContent {
            DejaloTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    var ready by remember { mutableStateOf(false) }
                    var hasProfile by remember { mutableStateOf(false) }
                    val profile by repository.observeProfile()
                        .collectAsStateWithLifecycle(initialValue = null)

                    LaunchedEffect(Unit) {
                        hasProfile = repository.observeProfile().first() != null
                        ready = true
                    }

                    if (!ready) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = DejaloColors.TealDeep)
                        }
                        return@Surface
                    }

                    val navController = rememberNavController()
                    val start = if (!hasProfile) Routes.Onboarding else Routes.Home

                    LaunchedEffect(openEmergency, hasProfile) {
                        if (openEmergency && hasProfile) {
                            navController.navigate(Routes.Emergency)
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = start
                    ) {
                        composable(Routes.Onboarding) {
                            OnboardingScreen(
                                repository = repository,
                                onFinished = {
                                    WidgetUpdater.enqueue(this@MainActivity)
                                    navController.navigate(Routes.Home) {
                                        popUpTo(Routes.Onboarding) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Routes.Home) {
                            DashboardScreen(
                                repository = repository,
                                onEmergency = { navController.navigate(Routes.Emergency) },
                                onNotNow = { navController.navigate(Routes.NotNow) },
                                onGames = { navController.navigate(Routes.Games) },
                                onHealth = { navController.navigate(Routes.Health) },
                                onAchievements = { navController.navigate(Routes.Achievements) },
                                onRelapse = { navController.navigate(Routes.Relapse) },
                                onDocs = { navController.navigate(Routes.Docs) },
                                onSettings = { navController.navigate(Routes.Settings) },
                                onAbout = { navController.navigate(Routes.About) },
                                onAnsiaHistory = { navController.navigate(Routes.AnsiaHistory) },
                                onRoutines = { navController.navigate(Routes.Routines) },
                                onRiskZones = { navController.navigate(Routes.RiskZones) },
                                onLearnings = { navController.navigate(Routes.Learnings) }
                            )
                        }
                        composable(Routes.Games) {
                            GamesHubScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(Routes.Health) {
                            HealthScreen(
                                repository = repository,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(Routes.Emergency) {
                            EmergencyScreen(
                                repository = repository,
                                profile = profile,
                                openGames = false,
                                onOpenRoutines = { navController.navigate(Routes.Routines) },
                                onBack = {
                                    if (!navController.popBackStack()) {
                                        navController.navigate(Routes.Home) {
                                            popUpTo(Routes.Home) { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }
                        composable(Routes.Achievements) {
                            AchievementsScreen(
                                repository = repository,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(Routes.Relapse) {
                            RelapseScreen(
                                repository = repository,
                                onBack = {
                                    WidgetUpdater.enqueue(this@MainActivity)
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(Routes.Docs) {
                            DocsScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(Routes.Settings) {
                            SettingsScreen(
                                repository = repository,
                                onBack = {
                                    WidgetUpdater.enqueue(this@MainActivity)
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(Routes.About) {
                            AboutScreen(
                                onBack = { navController.popBackStack() },
                                onDocs = { navController.navigate(Routes.Docs) }
                            )
                        }
                        composable(Routes.AnsiaHistory) {
                            AnsiaHistoryScreen(
                                repository = repository,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(Routes.Routines) {
                            RoutinesScreen(
                                repository = repository,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(
                            route = Routes.RoutinesWithSituation,
                            arguments = listOf(
                                navArgument("situation") { type = NavType.StringType }
                            )
                        ) { entry ->
                            val situation = entry.arguments?.getString("situation").orEmpty()
                            RoutinesScreen(
                                repository = repository,
                                prefillSituation = situation,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(Routes.RiskZones) {
                            RiskZonesScreen(
                                repository = repository,
                                onBack = { navController.popBackStack() },
                                onEmergency = { navController.navigate(Routes.Emergency) }
                            )
                        }
                        composable(Routes.Learnings) {
                            LearningsScreen(
                                repository = repository,
                                onBack = { navController.popBackStack() },
                                onCreateRoutine = { situation ->
                                    navController.navigate(Routes.routinesFor(situation))
                                }
                            )
                        }
                        composable(Routes.NotNow) {
                            NotNowScreen(
                                repository = repository,
                                onBack = { navController.popBackStack() },
                                onEmergency = { navController.navigate(Routes.Emergency) }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun requestNotificationsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
