package com.dejalo.app.data

import com.dejalo.app.data.local.DejaloDatabase
import com.dejalo.app.data.local.entity.AlternativeRoutineEntity
import com.dejalo.app.data.local.entity.BadgeEntity
import com.dejalo.app.data.local.entity.CravingEventEntity
import com.dejalo.app.data.local.entity.RelapseEventEntity
import com.dejalo.app.data.local.entity.UserProfileEntity
import com.dejalo.app.domain.BadgeCatalog
import com.dejalo.app.domain.LiveMetrics
import com.dejalo.app.domain.LiveQuitStats
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class QuitRepository(private val db: DejaloDatabase) {

    private val profileDao = db.userProfileDao()
    private val cravingDao = db.cravingDao()
    private val relapseDao = db.relapseDao()
    private val badgeDao = db.badgeDao()
    private val routineDao = db.alternativeRoutineDao()

    fun observeProfile(): Flow<UserProfileEntity?> = profileDao.observeProfile()

    fun observeCravings(): Flow<List<CravingEventEntity>> = cravingDao.observeAll()

    fun observeRelapses(): Flow<List<RelapseEventEntity>> = relapseDao.observeAll()

    fun observeBadges(): Flow<List<BadgeEntity>> = badgeDao.observeAll()

    fun observeRoutines(): Flow<List<AlternativeRoutineEntity>> = routineDao.observeAll()

    fun observeRoutineFor(situation: String): Flow<AlternativeRoutineEntity?> =
        routineDao.observeBySituation(situation)

    suspend fun saveRoutine(routine: AlternativeRoutineEntity) {
        val existing = routineDao.getBySituation(routine.situation)
        if (existing != null) {
            routineDao.update(
                routine.copy(
                    id = existing.id,
                    updatedAtMillis = System.currentTimeMillis()
                )
            )
        } else {
            routineDao.upsert(
                routine.copy(id = 0, updatedAtMillis = System.currentTimeMillis())
            )
        }
    }

    suspend fun deleteRoutine(id: Long) {
        routineDao.deleteById(id)
    }

    private val ticker: Flow<Long> = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(1_000)
        }
    }

    fun observeLiveStats(): Flow<LiveQuitStats?> = combine(
        profileDao.observeProfile(),
        relapseDao.observeAll(),
        ticker
    ) { profile, relapses, now ->
        if (profile == null) return@combine null
        val relapsedCigs = relapses.sumOf { it.cigarettes }
        LiveQuitStats.from(
            quitAtMillis = profile.quitAtMillis,
            nowMillis = now,
            cigarettesPerDay = profile.cigarettesPerDay,
            cigarettesPerPack = profile.cigarettesPerPack,
            packPriceEuros = profile.packPriceEuros,
            relapsedCigarettes = relapsedCigs,
            relapseAtMillis = relapses.map { it.occurredAtMillis },
            savingsGoalEuros = profile.savingsGoalEuros
        )
    }

    suspend fun saveOnboarding(profile: UserProfileEntity) {
        profileDao.upsert(profile)
    }

    suspend fun updateSavingsGoal(label: String, euros: Double) {
        val current = profileDao.getProfile() ?: return
        profileDao.upsert(
            current.copy(
                savingsGoalLabel = label,
                savingsGoalEuros = euros
            )
        )
    }

    suspend fun logCraving(
        trigger: String,
        durationSeconds: Int,
        notes: String = "",
        intensityInitial: Int = -1,
        intensityFinal: Int = -1,
        tools: List<String> = emptyList()
    ) {
        cravingDao.insert(
            CravingEventEntity(
                triggeredAtMillis = System.currentTimeMillis(),
                trigger = trigger,
                durationSeconds = durationSeconds,
                notes = notes,
                intensityInitial = intensityInitial,
                intensityFinal = intensityFinal,
                toolsCsv = tools.filter { it.isNotBlank() }.joinToString("|")
            )
        )
    }

    /**
     * Registra una recaída sin borrar el historial.
     * La racha actual se reinicia; el progreso acumulado, el ahorro y
     * los cigarrillos evitados siguen contando desde la fecha de abandono.
     */
    suspend fun logRelapse(
        cigarettes: Int,
        cause: String,
        notes: String,
        nextPlan: String = ""
    ) {
        relapseDao.insert(
            RelapseEventEntity(
                occurredAtMillis = System.currentTimeMillis(),
                cigarettes = cigarettes.coerceAtLeast(1),
                cause = cause,
                notes = notes,
                nextPlan = nextPlan.trim()
            )
        )
    }

    suspend fun evaluateBadges(stats: LiveQuitStats) {
        val metrics = LiveMetrics(
            cleanDays = stats.days + stats.hours / 24.0,
            moneySaved = stats.moneySavedEuros,
            cigarettesAvoided = stats.cigarettesAvoided,
            consecutiveCleanDays = stats.consecutiveCleanDays
        )
        val unlocked = badgeDao.getUnlockedIds().toSet()
        val now = System.currentTimeMillis()
        BadgeCatalog.definitions
            .filter { it.id !in unlocked && it.predicate(metrics) }
            .forEach { badgeDao.unlock(BadgeEntity(it.id, now)) }
    }

    fun motivatorsOf(profile: UserProfileEntity): List<String> {
        val fromCsv = profile.motivatorsCsv
            .split('|')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        val custom = profile.customMotivator.trim()
        return if (custom.isNotEmpty()) fromCsv + custom else fromCsv
    }
}
