package com.dejalo.app.domain

import kotlin.math.max

/**
 * Motor de cálculo offline-first según especificaciones técnicas.
 *
 * Ahorro (€) = (cig/día ÷ cig/paquete × precio paquete) × días
 * Cigarrillos evitados = cig/día × días
 *
 * Tiempo de vida recuperado: ~11 minutos por cigarrillo evitado (estimación habitual en apps de cesación).
 */
object QuitMetricsCalculator {

    private const val MINUTES_PER_CIGARETTE = 11.0
    private const val MS_PER_DAY = 86_400_000.0

    fun elapsedMillis(quitAtMillis: Long, nowMillis: Long): Long =
        max(0L, nowMillis - quitAtMillis)

    fun daysElapsed(quitAtMillis: Long, nowMillis: Long): Double =
        elapsedMillis(quitAtMillis, nowMillis) / MS_PER_DAY

    fun dailySpendEuros(
        cigarettesPerDay: Double,
        cigarettesPerPack: Int,
        packPriceEuros: Double
    ): Double {
        if (cigarettesPerPack <= 0) return 0.0
        return (cigarettesPerDay / cigarettesPerPack) * packPriceEuros
    }

    fun moneySavedEuros(
        cigarettesPerDay: Double,
        cigarettesPerPack: Int,
        packPriceEuros: Double,
        quitAtMillis: Long,
        nowMillis: Long,
        relapsedCigarettes: Int = 0
    ): Double {
        val gross = dailySpendEuros(cigarettesPerDay, cigarettesPerPack, packPriceEuros) *
            daysElapsed(quitAtMillis, nowMillis)
        val costPerCigarette = if (cigarettesPerPack > 0) {
            packPriceEuros / cigarettesPerPack
        } else {
            0.0
        }
        return max(0.0, gross - relapsedCigarettes * costPerCigarette)
    }

    fun cigarettesAvoided(
        cigarettesPerDay: Double,
        quitAtMillis: Long,
        nowMillis: Long,
        relapsedCigarettes: Int = 0
    ): Double {
        val gross = cigarettesPerDay * daysElapsed(quitAtMillis, nowMillis)
        return max(0.0, gross - relapsedCigarettes)
    }

    fun lifeRegainedMinutes(
        cigarettesPerDay: Double,
        quitAtMillis: Long,
        nowMillis: Long,
        relapsedCigarettes: Int = 0
    ): Double {
        return cigarettesAvoided(
            cigarettesPerDay,
            quitAtMillis,
            nowMillis,
            relapsedCigarettes
        ) * MINUTES_PER_CIGARETTE
    }

    /**
     * Mejor tramo limpio entre abandono → recaídas → ahora.
     * Las recaídas son puntos en el tiempo; no borran el historial.
     */
    fun bestStreakDays(
        quitAtMillis: Long,
        relapseAtMillis: List<Long>,
        nowMillis: Long
    ): Double {
        val sorted = relapseAtMillis
            .filter { it > quitAtMillis && it <= nowMillis }
            .distinct()
            .sorted()
        val boundaries = buildList {
            add(quitAtMillis)
            addAll(sorted)
            add(nowMillis)
        }
        var best = 0.0
        for (i in 0 until boundaries.lastIndex) {
            val segment = daysElapsed(boundaries[i], boundaries[i + 1])
            if (segment > best) best = segment
        }
        return best
    }

    fun currentStreakStartMillis(
        quitAtMillis: Long,
        lastRelapseAtMillis: Long?
    ): Long = lastRelapseAtMillis?.takeIf { it > quitAtMillis } ?: quitAtMillis
}
