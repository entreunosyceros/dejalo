package com.dejalo.app.domain.craving

import com.dejalo.app.data.local.entity.CravingEventEntity
import com.dejalo.app.data.local.entity.RelapseEventEntity
import com.dejalo.app.domain.relapse.RelapseAnalyzer

data class ProcessLearning(
    val title: String,
    val detail: String
)

data class ProcessLearnings(
    val items: List<ProcessLearning>,
    val emergencyWins: Int,
    val emergencyTotal: Int,
    val headline: String,
    val whatWorks: WhatWorksForMe? = null,
    val cravingRelapseLinks: List<CravingRelapseLink> = emptyList()
)

/**
 * Resume “lo que has aprendido” cruzando ansia, franjas, herramientas y recaídas.
 */
object CravingInsights {

    fun build(
        cravings: List<CravingEventEntity>,
        relapses: List<RelapseEventEntity> = emptyList()
    ): ProcessLearnings? {
        if (cravings.isEmpty() && relapses.isEmpty()) return null

        val items = mutableListOf<ProcessLearning>()
        val summary = CravingAnalyzer.summarize(cravings)
        val riskZones = CravingRiskDetector.detectZones(cravings)
        val relapseSummary = RelapseAnalyzer.summarize(relapses)
        val whatWorks = ToolEffectivenessAnalyzer.analyze(cravings)
        val cravingRelapseLinks = CravingRelapsePatternAnalyzer.analyze(cravings, relapses)

        summary?.topTrigger?.let { trigger ->
            items += ProcessLearning(
                title = "Desencadenante frecuente",
                detail = "Tus ganas de fumar aparecen sobre todo con: $trigger (${summary.topTriggerCount} episodios)."
            )
        }
        summary?.peakHourBand?.let { band ->
            items += ProcessLearning(
                title = "Franja horaria",
                detail = "Suelen aparecer entre las ${"%02d:00".format(band.startHour)} y las ${"%02d:00".format(band.endHour)}."
            )
        }
        riskZones.firstOrNull()?.let { zone ->
            items += ProcessLearning(
                title = "Zona de riesgo",
                detail = "Patrón repetido en ${zone.timeLabel()} (${zone.daysWithHits} días con episodios)."
            )
        }
        summary?.averageDrop?.takeIf { summary.episodesWithIntensity > 0 }?.let { drop ->
            if (drop > 0) {
                items += ProcessLearning(
                    title = "Las herramientas ayudan",
                    detail = "De media el ansia baja ${"%.1f".format(drop)} puntos (0–10) tras usar el modo emergencia."
                )
            }
        }
        relapseSummary?.topCause?.let { cause ->
            items += ProcessLearning(
                title = "Recaídas: causa más habitual",
                detail = "Cuando has fumado, lo más frecuente ha sido: $cause. Usa eso para preparar la próxima vez."
            )
        }

        val wins = cravings.count { it.resolved }
        val total = cravings.size
        val headline = when {
            cravingRelapseLinks.any { it.relapseCount > 0 } -> {
                val link = cravingRelapseLinks.first { it.relapseCount > 0 }
                "${link.label}: ${link.cravingCount} episodios de ansia y ${link.relapseCount} recaída${if (link.relapseCount == 1) "" else "s"} relacionadas."
            }
            whatWorks?.ranking?.isNotEmpty() == true -> {
                val top = whatWorks.ranking.first()
                "Tu herramienta con mayor bajada media: ${top.label} (↓ ${ToolEffectivenessAnalyzer.formatDrop(top.averageDrop)})."
            }
            total == 0 -> "Sigue registrando episodios: aquí irá tu aprendizaje."
            wins == total && total > 0 ->
                "En $total de $total ocasiones superaste el ansia sin fumar usando el modo emergencia."
            wins > 0 ->
                "En $wins de $total ocasiones conseguiste superar el ansia sin fumar con el modo emergencia."
            else -> "Cada episodio registrado te ayuda a conocer tus riesgos."
        }

        if (items.isEmpty() && total == 0 && whatWorks == null && cravingRelapseLinks.isEmpty()) return null

        return ProcessLearnings(
            items = items,
            emergencyWins = wins,
            emergencyTotal = total,
            headline = headline,
            whatWorks = whatWorks,
            cravingRelapseLinks = cravingRelapseLinks
        )
    }
}
