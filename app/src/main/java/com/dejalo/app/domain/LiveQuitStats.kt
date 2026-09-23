package com.dejalo.app.domain

data class LiveQuitStats(
    val elapsedMillis: Long,
    /** Reloj del progreso acumulado desde la fecha de abandono (con interrupciones). */
    val days: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    /** Reloj de la racha actual (desde la última recaída o el abandono). */
    val streakDays: Int,
    val streakHours: Int,
    val streakMinutes: Int,
    val streakSeconds: Int,
    val moneySavedEuros: Double,
    val cigarettesAvoided: Double,
    val lifeRegainedMinutes: Double,
    val savingsGoalProgress: Float,
    /** Días (fraccionarios) de la racha actual. */
    val consecutiveCleanDays: Double,
    /** Mejor racha alcanzada en cualquier tramo limpio. */
    val bestStreakDays: Double,
    /** Días totales desde el abandono (progreso acumulado). */
    val accumulatedCleanDays: Double,
    /** true si hubo al menos una recaída tras el abandono. */
    val hasInterruptions: Boolean
) {
    companion object {
        fun from(
            quitAtMillis: Long,
            nowMillis: Long,
            cigarettesPerDay: Double,
            cigarettesPerPack: Int,
            packPriceEuros: Double,
            relapsedCigarettes: Int,
            relapseAtMillis: List<Long>,
            savingsGoalEuros: Double
        ): LiveQuitStats {
            val elapsed = QuitMetricsCalculator.elapsedMillis(quitAtMillis, nowMillis)
            val (days, hours, minutes, seconds) = partsOf(elapsed)

            val money = QuitMetricsCalculator.moneySavedEuros(
                cigarettesPerDay,
                cigarettesPerPack,
                packPriceEuros,
                quitAtMillis,
                nowMillis,
                relapsedCigarettes
            )
            val cigs = QuitMetricsCalculator.cigarettesAvoided(
                cigarettesPerDay,
                quitAtMillis,
                nowMillis,
                relapsedCigarettes
            )
            val life = QuitMetricsCalculator.lifeRegainedMinutes(
                cigarettesPerDay,
                quitAtMillis,
                nowMillis,
                relapsedCigarettes
            )
            val lastRelapse = relapseAtMillis.maxOrNull()
            val streakStart = QuitMetricsCalculator.currentStreakStartMillis(
                quitAtMillis,
                lastRelapse
            )
            val streakElapsed = QuitMetricsCalculator.elapsedMillis(streakStart, nowMillis)
            val (streakDays, streakHours, streakMinutes, streakSeconds) = partsOf(streakElapsed)
            val consecutive = QuitMetricsCalculator.daysElapsed(streakStart, nowMillis)
            val accumulated = QuitMetricsCalculator.daysElapsed(quitAtMillis, nowMillis)
            val best = QuitMetricsCalculator.bestStreakDays(
                quitAtMillis,
                relapseAtMillis,
                nowMillis
            )
            val interruptions = relapseAtMillis.any { it > quitAtMillis && it <= nowMillis }
            val goalProgress = if (savingsGoalEuros > 0) {
                (money / savingsGoalEuros).toFloat().coerceIn(0f, 1f)
            } else {
                0f
            }

            return LiveQuitStats(
                elapsedMillis = elapsed,
                days = days,
                hours = hours,
                minutes = minutes,
                seconds = seconds,
                streakDays = streakDays,
                streakHours = streakHours,
                streakMinutes = streakMinutes,
                streakSeconds = streakSeconds,
                moneySavedEuros = money,
                cigarettesAvoided = cigs,
                lifeRegainedMinutes = life,
                savingsGoalProgress = goalProgress,
                consecutiveCleanDays = consecutive,
                bestStreakDays = best,
                accumulatedCleanDays = accumulated,
                hasInterruptions = interruptions
            )
        }

        private fun partsOf(elapsedMillis: Long): Quad {
            val totalSeconds = elapsedMillis / 1000
            return Quad(
                days = (totalSeconds / 86_400).toInt(),
                hours = ((totalSeconds % 86_400) / 3_600).toInt(),
                minutes = ((totalSeconds % 3_600) / 60).toInt(),
                seconds = (totalSeconds % 60).toInt()
            )
        }

        private data class Quad(
            val days: Int,
            val hours: Int,
            val minutes: Int,
            val seconds: Int
        )
    }
}
