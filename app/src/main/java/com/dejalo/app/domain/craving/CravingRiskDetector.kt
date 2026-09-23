package com.dejalo.app.domain.craving

import com.dejalo.app.data.local.entity.CravingEventEntity
import java.util.Calendar
import java.util.concurrent.TimeUnit

enum class RiskPhase {
    /** Estamos dentro de la franja de riesgo. */
    ACTIVE,
    /** La franja empieza en menos de ~45 minutos. */
    APPROACHING
}

data class RiskZone(
    val startHour: Int,
    val endHour: Int,
    /** Episodios en esta franja (ventana reciente). */
    val episodeCount: Int,
    /** Días distintos (en la ventana) con al menos un episodio en la franja. */
    val daysWithHits: Int,
    val topTrigger: String?
) {
    fun timeLabel(): String =
        "%02d:00–%02d:00".format(startHour, endHour)
}

data class RiskAlert(
    val zone: RiskZone,
    val phase: RiskPhase,
    val title: String,
    val body: String
)

/**
 * Detecta “zonas de riesgo” horarias a partir del historial de ansia (offline).
 */
object CravingRiskDetector {

    private const val LOOKBACK_DAYS = 7
    private const val MIN_EPISODES = 3
    private const val MIN_DAYS_WITH_HITS = 2
    private const val APPROACH_MINUTES = 45

    fun detectZones(
        events: List<CravingEventEntity>,
        nowMillis: Long = System.currentTimeMillis()
    ): List<RiskZone> {
        val since = nowMillis - TimeUnit.DAYS.toMillis(LOOKBACK_DAYS.toLong())
        val recent = events.filter { it.triggeredAtMillis >= since }
        if (recent.size < MIN_EPISODES) return emptyList()

        val cal = Calendar.getInstance()
        // Contadores por hora y por (día del año + hora) para días distintos
        val byHour = IntArray(24)
        val daysByHour = Array(24) { mutableSetOf<Int>() }
        val triggersByHour = Array(24) { mutableMapOf<String, Int>() }

        recent.forEach { ev ->
            cal.timeInMillis = ev.triggeredAtMillis
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val dayKey = cal.get(Calendar.YEAR) * 400 + cal.get(Calendar.DAY_OF_YEAR)
            byHour[hour]++
            daysByHour[hour].add(dayKey)
            val t = ev.trigger
            triggersByHour[hour][t] = (triggersByHour[hour][t] ?: 0) + 1
        }

        val zones = mutableListOf<RiskZone>()
        var h = 0
        while (h <= 22) {
            val count = byHour[h] + byHour[h + 1]
            val days = (daysByHour[h] + daysByHour[h + 1]).size
            if (count >= MIN_EPISODES && days >= MIN_DAYS_WITH_HITS) {
                val triggerMap = mutableMapOf<String, Int>()
                triggersByHour[h].forEach { (k, v) -> triggerMap[k] = (triggerMap[k] ?: 0) + v }
                triggersByHour[h + 1].forEach { (k, v) -> triggerMap[k] = (triggerMap[k] ?: 0) + v }
                val top = triggerMap.maxByOrNull { it.value }?.key
                zones += RiskZone(
                    startHour = h,
                    endHour = h + 2,
                    episodeCount = count,
                    daysWithHits = days,
                    topTrigger = top
                )
                h += 2 // evitar solapes consecutivos
            } else {
                h++
            }
        }
        return zones.sortedByDescending { it.episodeCount }
    }

    fun alertForNow(
        events: List<CravingEventEntity>,
        nowMillis: Long = System.currentTimeMillis()
    ): RiskAlert? {
        val zones = detectZones(events, nowMillis)
        if (zones.isEmpty()) return null

        val cal = Calendar.getInstance()
        cal.timeInMillis = nowMillis
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val nowMinutes = hour * 60 + minute

        var best: Pair<RiskZone, RiskPhase>? = null
        for (zone in zones) {
            val start = zone.startHour * 60
            val end = zone.endHour * 60
            when {
                nowMinutes in start until end -> {
                    best = zone to RiskPhase.ACTIVE
                    break
                }
                nowMinutes in (start - APPROACH_MINUTES) until start -> {
                    if (best == null || best.second != RiskPhase.ACTIVE) {
                        best = zone to RiskPhase.APPROACHING
                    }
                }
            }
        }
        val (zone, phase) = best ?: return null
        return RiskAlert(
            zone = zone,
            phase = phase,
            title = if (phase == RiskPhase.APPROACHING) {
                "Zona de riesgo cerca"
            } else {
                "Estás en una zona de riesgo"
            },
            body = buildBody(zone, phase)
        )
    }

    private fun buildBody(zone: RiskZone, phase: RiskPhase): String {
        val daysPart = when (zone.daysWithHits) {
            1 -> "1 día"
            else -> "${zone.daysWithHits} días"
        }
        val base = "En los últimos $LOOKBACK_DAYS días has registrado ganas de fumar " +
            "entre las ${"%02d:00".format(zone.startHour)} y las ${"%02d:00".format(zone.endHour)} " +
            "($daysPart con episodios)."
        val triggerPart = zone.topTrigger?.let { " Suele aparecer con: $it." } ?: ""
        val prep = when (phase) {
            RiskPhase.APPROACHING -> " ¿Preparado? Abre el modo emergencia si lo necesitas."
            RiskPhase.ACTIVE -> " Si aparecen las ganas, usa el modo emergencia."
        }
        return base + triggerPart + prep
    }
}
