package com.dejalo.app.domain.craving

import com.dejalo.app.data.local.entity.CravingEventEntity
import com.dejalo.app.data.local.entity.RelapseEventEntity
import java.util.Locale

/**
 * Patrón descriptivo: un mismo contexto aparece en ansias y (a veces) en recaídas.
 */
data class CravingRelapseLink(
    /** Etiqueta canónica para mostrar (p. ej. «Café»). */
    val label: String,
    /** Situación alineada con rutinas alternativas. */
    val routineSituation: String,
    val cravingCount: Int,
    val averageIntensity: Float?,
    val relapseCount: Int,
    val narrative: String
)

object CravingRelapsePatternAnalyzer {

    private const val MIN_CRAVINGS = 2

    /**
     * Normaliza etiquetas de desencadenante/causa a una clave comparable.
     */
    fun canonicalKey(raw: String): String? {
        val t = raw.trim().lowercase(Locale("es", "ES"))
        if (t.isEmpty()) return null
        return when {
            t.contains("café") || t.contains("cafe") -> "cafe"
            t.contains("estrés") || t.contains("estres") || t.contains("discusión") || t.contains("discusion") -> "estres"
            t.contains("alcohol") -> "alcohol"
            t.contains("aburr") -> "aburrimiento"
            t.contains("social") || t.contains("amigo") || t.contains("entorno") -> "social"
            t.contains("solo uno") || t.contains("impulso") -> "impulso"
            t.contains("ahora no") -> null // no es un contexto situacional
            t.contains("otro") -> "otro"
            else -> t.take(32)
        }
    }

    fun displayLabel(key: String): String = when (key) {
        "cafe" -> "Café"
        "estres" -> "Estrés"
        "alcohol" -> "Alcohol"
        "aburrimiento" -> "Aburrimiento"
        "social" -> "Entorno social"
        "impulso" -> "Impulso"
        "otro" -> "Otro"
        else -> key.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es", "ES")) else it.toString() }
    }

    /** Situación del catálogo de rutinas (mejor match). */
    fun routineSituationFor(key: String): String = when (key) {
        "cafe" -> "Café"
        "estres" -> "Estrés"
        "alcohol" -> "Alcohol"
        "aburrimiento" -> "Aburrimiento"
        "social" -> "Entorno social"
        else -> displayLabel(key)
    }

    fun analyze(
        cravings: List<CravingEventEntity>,
        relapses: List<RelapseEventEntity>
    ): List<CravingRelapseLink> {
        if (cravings.isEmpty()) return emptyList()

        data class Agg(
            var cravingCount: Int = 0,
            val intensities: MutableList<Int> = mutableListOf(),
            var relapseCount: Int = 0
        )

        val map = mutableMapOf<String, Agg>()

        cravings.forEach { ev ->
            val key = canonicalKey(ev.trigger) ?: return@forEach
            val agg = map.getOrPut(key) { Agg() }
            agg.cravingCount++
            if (ev.intensityInitial in 0..10) agg.intensities.add(ev.intensityInitial)
        }
        relapses.forEach { ev ->
            val key = canonicalKey(ev.cause) ?: return@forEach
            val agg = map.getOrPut(key) { Agg() }
            agg.relapseCount += 1
        }

        return map.entries
            .asSequence()
            .filter { it.value.cravingCount >= MIN_CRAVINGS }
            .map { (key, agg) ->
                val label = displayLabel(key)
                val avg = agg.intensities.takeIf { it.isNotEmpty() }?.average()?.toFloat()
                val narrative = buildNarrative(label, agg.cravingCount, avg, agg.relapseCount)
                CravingRelapseLink(
                    label = label,
                    routineSituation = routineSituationFor(key),
                    cravingCount = agg.cravingCount,
                    averageIntensity = avg,
                    relapseCount = agg.relapseCount,
                    narrative = narrative
                )
            }
            .sortedWith(
                compareByDescending<CravingRelapseLink> { it.relapseCount }
                    .thenByDescending { it.cravingCount }
                    .thenByDescending { it.averageIntensity ?: 0f }
            )
            .toList()
    }

    private fun buildNarrative(
        label: String,
        cravingCount: Int,
        avg: Float?,
        relapseCount: Int
    ): String {
        val lower = label.lowercase(Locale("es", "ES"))
        return when {
            relapseCount > 0 ->
                "$label aparece con frecuencia en tus episodios de ansia" +
                    (avg?.let { " (intensidad media ${fmt(it)}/10)" } ?: "") +
                    " y en algunos registros de recaída" +
                    if (relapseCount == 1) "." else " ($relapseCount)."
            else ->
                "Tienes $cravingCount episodios de ansia relacionados con $lower" +
                    (avg?.let { ", con intensidad media ${fmt(it)}/10" } ?: "") +
                    ". Aún no hay recaídas registradas con esa causa; una rutina alternativa puede ayudar a prevenirlas."
        }
    }

    private fun fmt(v: Float): String =
        String.format(Locale("es", "ES"), "%.1f", v)
}
