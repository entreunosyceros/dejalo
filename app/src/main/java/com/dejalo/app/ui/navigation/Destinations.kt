package com.dejalo.app.ui.navigation

object Routes {
    const val Onboarding = "onboarding"
    const val Home = "home"
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

    fun routinesFor(situation: String): String =
        "routines/${android.net.Uri.encode(situation)}"
}
