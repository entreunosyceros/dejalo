package com.dejalo.app.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class QuitMetricsCalculatorTest {

    @Test
    fun moneySaved_matchesSpecFormula() {
        // 20 cig/día, 20 cig/paquete, 5€ → 5€/día × 2 días = 10€
        val quitAt = 0L
        val now = 2 * 86_400_000L
        val saved = QuitMetricsCalculator.moneySavedEuros(
            cigarettesPerDay = 20.0,
            cigarettesPerPack = 20,
            packPriceEuros = 5.0,
            quitAtMillis = quitAt,
            nowMillis = now
        )
        assertEquals(10.0, saved, 0.001)
    }

    @Test
    fun cigarettesAvoided_matchesSpecFormula() {
        val quitAt = 0L
        val now = 3 * 86_400_000L
        val avoided = QuitMetricsCalculator.cigarettesAvoided(
            cigarettesPerDay = 10.0,
            quitAtMillis = quitAt,
            nowMillis = now
        )
        assertEquals(30.0, avoided, 0.001)
    }

    @Test
    fun relapseSubtractsFromTotals() {
        val quitAt = 0L
        val now = 2 * 86_400_000L
        val avoided = QuitMetricsCalculator.cigarettesAvoided(
            cigarettesPerDay = 20.0,
            quitAtMillis = quitAt,
            nowMillis = now,
            relapsedCigarettes = 5
        )
        assertEquals(35.0, avoided, 0.001)
    }

    @Test
    fun bestStreak_picksLongestCleanSegment() {
        val day = 86_400_000L
        val quitAt = 0L
        // 23 días limpios, recaída, 15 días, recaída, 3 días actuales → mejor = 23
        val relapses = listOf(23 * day, 38 * day)
        val now = 41 * day
        val best = QuitMetricsCalculator.bestStreakDays(quitAt, relapses, now)
        assertEquals(23.0, best, 0.001)
        val currentStart = QuitMetricsCalculator.currentStreakStartMillis(quitAt, relapses.maxOrNull())
        assertEquals(3.0, QuitMetricsCalculator.daysElapsed(currentStart, now), 0.001)
        assertEquals(41.0, QuitMetricsCalculator.daysElapsed(quitAt, now), 0.001)
    }

    @Test
    fun bestStreak_withoutRelapses_equalsAccumulated() {
        val quitAt = 0L
        val now = 10 * 86_400_000L
        assertEquals(10.0, QuitMetricsCalculator.bestStreakDays(quitAt, emptyList(), now), 0.001)
    }
}
