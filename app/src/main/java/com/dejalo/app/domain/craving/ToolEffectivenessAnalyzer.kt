package com.dejalo.app.domain.craving

import com.dejalo.app.data.local.entity.CravingEventEntity
import java.util.Locale

data class ToolEffectiveness(
    val toolKey: String,
    val label: String,
    val episodeCount: Int,
    /** Media de (inicial − final) en episodios donde se usó esta herramienta. */
    val averageDrop: Float
)

data class TriggerToolInsight(
    val trigger: String,
    val betterToolLabel: String,
    val betterDrop: Float,
    val weakerToolLabel: String,
    val weakerDrop: Float,
    val sampleNote: String
)

data class WhatWorksForMe(
    val ranking: List<ToolEffectiveness>,
    val triggerInsight: TriggerToolInsight?,
    /** Texto corto para el vacío / disclaimer. */
    val disclaimer: String =
        "Estadística descriptiva de tus propios episodios (no es consejo médico ni una predicción)."
)

/**
 * Calcula qué herramientas se asocian a mayor bajada de intensidad
 * en los datos del usuario (offline, sin IA).
 */
object ToolEffectivenessAnalyzer {

    private const val MIN_EPISODES_FOR_RANK = 1
    private const val MIN_EPISODES_FOR_COMPARE = 2

    fun analyze(events: List<CravingEventEntity>): WhatWorksForMe? {
        val withIntensity = events.filter {
            it.intensityInitial in 0..10 &&
                it.intensityFinal in 0..10 &&
                it.toolsCsv.isNotBlank()
        }
        if (withIntensity.isEmpty()) return null

        val dropsByTool = mutableMapOf<String, MutableList<Int>>()
        withIntensity.forEach { ev ->
            val drop = ev.intensityDrop ?: return@forEach
            parseToolKeys(ev.toolsCsv).forEach { key ->
                dropsByTool.getOrPut(key) { mutableListOf() }.add(drop)
            }
        }

        val ranking = dropsByTool
            .map { (key, drops) ->
                ToolEffectiveness(
                    toolKey = key,
                    label = CravingAnalyzer.toolLabel(key),
                    episodeCount = drops.size,
                    averageDrop = drops.average().toFloat()
                )
            }
            .filter { it.episodeCount >= MIN_EPISODES_FOR_RANK }
            .sortedWith(
                compareByDescending<ToolEffectiveness> { it.averageDrop }
                    .thenByDescending { it.episodeCount }
                    .thenBy { it.label }
            )

        if (ranking.isEmpty()) return null

        return WhatWorksForMe(
            ranking = ranking,
            triggerInsight = bestTriggerToolInsight(withIntensity)
        )
    }

    /**
     * Para el desencadenante más frecuente con datos suficientes,
     * compara la herramienta con mejor vs peor media de bajada.
     */
    private fun bestTriggerToolInsight(events: List<CravingEventEntity>): TriggerToolInsight? {
        val byTrigger = events.groupBy { it.trigger }
        val candidateTriggers = byTrigger.entries
            .filter { it.value.size >= MIN_EPISODES_FOR_COMPARE }
            .sortedByDescending { it.value.size }

        for ((trigger, list) in candidateTriggers) {
            val toolDrops = mutableMapOf<String, MutableList<Int>>()
            list.forEach { ev ->
                val drop = ev.intensityDrop ?: return@forEach
                parseToolKeys(ev.toolsCsv).forEach { key ->
                    toolDrops.getOrPut(key) { mutableListOf() }.add(drop)
                }
            }
            val ranked = toolDrops
                .map { (key, drops) ->
                    Triple(key, drops.average().toFloat(), drops.size)
                }
                .filter { it.third >= MIN_EPISODES_FOR_COMPARE }
                .sortedByDescending { it.second }

            if (ranked.size < 2) continue
            val best = ranked.first()
            val worst = ranked.last()
            if (best.second <= worst.second + 0.3f) continue // diferencia demasiado pequeña

            return TriggerToolInsight(
                trigger = trigger,
                betterToolLabel = CravingAnalyzer.toolLabel(best.first),
                betterDrop = best.second,
                weakerToolLabel = CravingAnalyzer.toolLabel(worst.first),
                weakerDrop = worst.second,
                sampleNote = "Basado en ${list.size} episodios con «$trigger»."
            )
        }
        return null
    }

    fun parseToolKeys(toolsCsv: String): List<String> =
        toolsCsv.split('|')
            .map { it.trim().lowercase() }
            .filter { it.isNotEmpty() }
            .distinct()

    fun formatDrop(drop: Float): String =
        String.format(Locale("es", "ES"), "%.1f", drop)
}
