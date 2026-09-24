package com.dejalo.app.ui.navigation

object Routes {
    const val Onboarding = "onboarding"
    const val Main = "main"
    const val Home = "home"
    const val ProgressHub = "progress_hub"
    const val ToolsHub = "tools_hub"
    const val SettingsHub = "settings_hub"
    const val Health = "health"
    const val Achievements = "achievements"
    const val Relapse = "relapse"
    const val Emergency = "emergency"
    const val Games = "games"
    const val Docs = "docs"
    const val Settings = "settings"
    const val About = "about"
    const val AnsiaHistory = "ansia_history"
    const val Routines = "routines"
    const val RoutinesWithSituation = "routines/{situation}"
    const val RiskZones = "risk_zones"
    const val Learnings = "learnings"
    const val NotNow = "not_now"
    const val Backup = "backup"

    fun routinesFor(situation: String): String =
        "routines/${android.net.Uri.encode(situation)}"
}

enum class MainTab(
    val route: String,
    val label: String
) {
    Home(Routes.Home, "Inicio"),
    Progress(Routes.ProgressHub, "Progreso"),
    Tools(Routes.ToolsHub, "Herramientas"),
    Settings(Routes.SettingsHub, "Ajustes")
}
