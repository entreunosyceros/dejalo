package com.dejalo.app.domain.relapse

import com.dejalo.app.data.local.entity.RelapseEventEntity

data class CauseCount(
    val cause: String,
    val count: Int
)

data class RelapseSummary(
    val totalRelapses: Int,
    val totalCigarettes: Int,
    val causesRanking: List<CauseCount>,
    val topCause: String?,
    val plansWithContent: Int
)

object RelapseAnalyzer {

    val causeOptions = listOf(
        "Estrés",
        "Alcohol",
        "Café",
        "Amigos fumando",
        "Discusión",
        "Aburrimiento",
        "Solo uno",
        "Otro"
    )

    fun summarize(events: List<RelapseEventEntity>): RelapseSummary? {
        if (events.isEmpty()) return null
        val ranking = events
            .groupingBy { it.cause.ifBlank { "Otro" } }
            .eachCount()
            .entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
            .map { CauseCount(it.key, it.value) }
        return RelapseSummary(
            totalRelapses = events.size,
            totalCigarettes = events.sumOf { it.cigarettes },
            causesRanking = ranking,
            topCause = ranking.firstOrNull()?.cause,
            plansWithContent = events.count { it.nextPlan.isNotBlank() }
        )
    }
}
