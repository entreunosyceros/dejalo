package com.dejalo.app.domain.craving

import com.dejalo.app.data.local.entity.CravingEventEntity
import java.util.Calendar

data class TriggerCount(
    val trigger: String,
    val count: Int
)

data class HourBand(
    /** Hora de inicio inclusive (0–23). */
    val startHour: Int,
    /** Hora de fin exclusive en reloj 24 h (p. ej. 18 para tramo 16–18). */
    val endHour: Int,
    val count: Int
)

data class CravingSummary(
    val totalEpisodes: Int,
    val episodesWithIntensity: Int,
    val averageInitial: Float?,
    val averageFinal: Float?,
    /** Media de puntos que baja el ansia (inicial − final). Positivo = mejoró. */
    val averageDrop: Float?,
    val topTrigger: String?,
    val topTriggerCount: Int,
    /** Ranking de desencadenantes (todos los tiempos). */
    val triggersAllTime: List<TriggerCount> = emptyList(),
    /** Ranking de esta semana (lunes 00:00 → ahora). */
    val triggersThisWeek: List<TriggerCount> = emptyList(),
    val topTriggerThisWeek: String? = null,
    val topTriggerThisWeekCount: Int = 0,
    /** Franja horaria más frecuente (ventanas de 2 h). */
    val peakHourBand: HourBand? = null
)

object CravingAnalyzer {

    fun summarize(
        events: List<CravingEventEntity>,
        nowMillis: Long = System.currentTimeMillis()
    ): CravingSummary? {
        if (events.isEmpty()) return null
        val withIntensity = events.filter {
            it.intensityInitial in 0..10 && it.intensityFinal in 0..10
        }
        val drops = withIntensity.mapNotNull { it.intensityDrop }
        val allTriggers = rankTriggers(events)
        val top = allTriggers.firstOrNull()

        val weekStart = startOfWeekMillis(nowMillis)
        val thisWeek = events.filter { it.triggeredAtMillis >= weekStart }
        val weekTriggers = rankTriggers(thisWeek)
        val weekTop = weekTriggers.firstOrNull()

        return CravingSummary(
            totalEpisodes = events.size,
            episodesWithIntensity = withIntensity.size,
            averageInitial = withIntensity.map { it.intensityInitial }.averageOrNull()?.toFloat(),
            averageFinal = withIntensity.map { it.intensityFinal }.averageOrNull()?.toFloat(),
            averageDrop = drops.averageOrNull()?.toFloat(),
            topTrigger = top?.trigger,
            topTriggerCount = top?.count ?: 0,
            triggersAllTime = allTriggers,
            triggersThisWeek = weekTriggers,
            topTriggerThisWeek = weekTop?.trigger,
            topTriggerThisWeekCount = weekTop?.count ?: 0,
            peakHourBand = peakTwoHourBand(events)
        )
    }

    fun rankTriggers(events: List<CravingEventEntity>): List<TriggerCount> =
        events.groupingBy { it.trigger }
            .eachCount()
            .entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
            .map { TriggerCount(it.key, it.value) }

    /**
     * Ventana de 2 horas con más episodios (p. ej. 16–18).
     * Requiere al menos 2 episodios para proponer franja.
     */
    fun peakTwoHourBand(events: List<CravingEventEntity>): HourBand? {
        if (events.size < 2) return null
        val cal = Calendar.getInstance()
        val byHour = IntArray(24)
        events.forEach { ev ->
            cal.timeInMillis = ev.triggeredAtMillis
            byHour[cal.get(Calendar.HOUR_OF_DAY)]++
        }
        var bestStart = 0
        var bestCount = -1
        for (h in 0..22) {
            val c = byHour[h] + byHour[h + 1]
            if (c > bestCount) {
                bestCount = c
                bestStart = h
            }
        }
        // Tramo 23–00
        val midnightBand = byHour[23] + byHour[0]
        if (midnightBand > bestCount) {
            bestCount = midnightBand
            bestStart = 23
        }
        if (bestCount <= 0) return null
        val endExclusive = if (bestStart == 23) 1 else bestStart + 2
        return HourBand(startHour = bestStart, endHour = endExclusive, count = bestCount)
    }

    fun episodeNumber(eventsNewestFirst: List<CravingEventEntity>, event: CravingEventEntity): Int {
        val oldestFirst = eventsNewestFirst.sortedBy { it.triggeredAtMillis }
        val idx = oldestFirst.indexOfFirst { it.id == event.id }
        return if (idx >= 0) idx + 1 else eventsNewestFirst.size
    }

    fun toolLabels(toolsCsv: String): List<String> =
        toolsCsv.split('|')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { toolLabel(it) }

    fun toolLabel(raw: String): String = when (raw.lowercase()) {
        "respiracion", "respiración" -> "Respiración"
        "juego", "juegos" -> "Minijuego"
        "burbujas", "bubble" -> "Burbujas"
        "reaccion", "reacción", "reaction" -> "Reacción rápida"
        "traza", "trace" -> "Traza el camino"
        "memoria", "memory" -> "Memoria"
        "numeros", "números", "numbers" -> "Orden numérico"
        "ola", "hold" -> "Aguanta la ola"
        "progresivo", "progressive" -> "Temporizador progresivo"
        "ritmo", "rhythm" -> "Ritmo suave"
        "sigue", "follow" -> "Sigue el punto"
        "tarjeta", "tarjetas", "motivadores" -> "Motivadores"
        "ahora_no", "ahora no" -> "Ahora no"
        else -> raw.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    fun formatDuration(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return "%d:%02d".format(m, s)
    }

    private fun startOfWeekMillis(nowMillis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = nowMillis
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        // Si "ahora" es domingo y firstDayOfWeek=MONDAY, DAY_OF_WEEK=MONDAY puede ir al próximo lunes
        // en algunos locales. Corregir: si el lunes calculado es futuro, restar 7 días.
        if (cal.timeInMillis > nowMillis) {
            cal.add(Calendar.DAY_OF_YEAR, -7)
        }
        return cal.timeInMillis
    }

    private fun List<Int>.averageOrNull(): Double? =
        if (isEmpty()) null else average()
}
