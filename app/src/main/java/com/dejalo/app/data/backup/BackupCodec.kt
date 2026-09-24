package com.dejalo.app.data.backup

import androidx.room.withTransaction
import com.dejalo.app.data.local.DejaloDatabase
import com.dejalo.app.data.local.entity.AlternativeRoutineEntity
import com.dejalo.app.data.local.entity.BadgeEntity
import com.dejalo.app.data.local.entity.CravingEventEntity
import com.dejalo.app.data.local.entity.RelapseEventEntity
import com.dejalo.app.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject

object BackupCodec {
    const val SCHEMA_VERSION = 1

    suspend fun exportJson(db: DejaloDatabase): String {
        val profile = db.userProfileDao().getProfile()
        val cravings = db.cravingDao().observeAll().first()
        val relapses = db.relapseDao().observeAll().first()
        val badges = db.badgeDao().observeAll().first()
        val routines = db.alternativeRoutineDao().observeAll().first()

        val root = JSONObject()
        root.put("schemaVersion", SCHEMA_VERSION)
        root.put("exportedAtMillis", System.currentTimeMillis())
        root.put("app", "dejalo")

        if (profile != null) {
            root.put(
                "profile",
                JSONObject()
                    .put("quitAtMillis", profile.quitAtMillis)
                    .put("cigarettesPerDay", profile.cigarettesPerDay)
                    .put("packPriceEuros", profile.packPriceEuros)
                    .put("cigarettesPerPack", profile.cigarettesPerPack)
                    .put("motivatorsCsv", profile.motivatorsCsv)
                    .put("customMotivator", profile.customMotivator)
                    .put("savingsGoalEuros", profile.savingsGoalEuros)
                    .put("savingsGoalLabel", profile.savingsGoalLabel)
                    .put("onboardingCompleted", profile.onboardingCompleted)
            )
        } else {
            root.put("profile", JSONObject.NULL)
        }

        root.put("cravings", JSONArray().apply {
            cravings.forEach { c ->
                put(
                    JSONObject()
                        .put("triggeredAtMillis", c.triggeredAtMillis)
                        .put("trigger", c.trigger)
                        .put("durationSeconds", c.durationSeconds)
                        .put("resolved", c.resolved)
                        .put("notes", c.notes)
                        .put("intensityInitial", c.intensityInitial)
                        .put("intensityFinal", c.intensityFinal)
                        .put("toolsCsv", c.toolsCsv)
                )
            }
        })
        root.put("relapses", JSONArray().apply {
            relapses.forEach { r ->
                put(
                    JSONObject()
                        .put("occurredAtMillis", r.occurredAtMillis)
                        .put("cigarettes", r.cigarettes)
                        .put("cause", r.cause)
                        .put("notes", r.notes)
                        .put("nextPlan", r.nextPlan)
                )
            }
        })
        root.put("badges", JSONArray().apply {
            badges.forEach { b ->
                put(
                    JSONObject()
                        .put("id", b.id)
                        .put("unlockedAtMillis", b.unlockedAtMillis)
                )
            }
        })
        root.put("routines", JSONArray().apply {
            routines.forEach { r ->
                put(
                    JSONObject()
                        .put("situation", r.situation)
                        .put("oldPattern", r.oldPattern)
                        .put("stepsCsv", r.stepsCsv)
                        .put("updatedAtMillis", r.updatedAtMillis)
                )
            }
        })
        return root.toString(2)
    }

    suspend fun importJson(db: DejaloDatabase, json: String) {
        val root = JSONObject(json)
        require(root.optString("app") == "dejalo" || root.has("profile")) {
            "Archivo no reconocido como copia de Déjalo!"
        }
        val version = root.optInt("schemaVersion", 1)
        require(version <= SCHEMA_VERSION) {
            "Copia de una versión más nueva (schema $version). Actualiza la app."
        }

        db.withTransaction {
            db.cravingDao().deleteAll()
            db.relapseDao().deleteAll()
            db.badgeDao().deleteAll()
            db.alternativeRoutineDao().deleteAll()
            db.userProfileDao().deleteAll()

            val profileJson = root.optJSONObject("profile")
            if (profileJson != null) {
                db.userProfileDao().upsert(
                    UserProfileEntity(
                        quitAtMillis = profileJson.getLong("quitAtMillis"),
                        cigarettesPerDay = profileJson.getDouble("cigarettesPerDay"),
                        packPriceEuros = profileJson.getDouble("packPriceEuros"),
                        cigarettesPerPack = profileJson.getInt("cigarettesPerPack"),
                        motivatorsCsv = profileJson.optString("motivatorsCsv"),
                        customMotivator = profileJson.optString("customMotivator"),
                        savingsGoalEuros = profileJson.optDouble("savingsGoalEuros", 0.0),
                        savingsGoalLabel = profileJson.optString("savingsGoalLabel"),
                        onboardingCompleted = profileJson.optBoolean("onboardingCompleted", true)
                    )
                )
            }

            val cravings = root.optJSONArray("cravings") ?: JSONArray()
            for (i in 0 until cravings.length()) {
                val c = cravings.getJSONObject(i)
                db.cravingDao().insert(
                    CravingEventEntity(
                        triggeredAtMillis = c.getLong("triggeredAtMillis"),
                        trigger = c.optString("trigger"),
                        durationSeconds = c.optInt("durationSeconds"),
                        resolved = c.optBoolean("resolved", true),
                        notes = c.optString("notes"),
                        intensityInitial = c.optInt("intensityInitial", -1),
                        intensityFinal = c.optInt("intensityFinal", -1),
                        toolsCsv = c.optString("toolsCsv")
                    )
                )
            }

            val relapses = root.optJSONArray("relapses") ?: JSONArray()
            for (i in 0 until relapses.length()) {
                val r = relapses.getJSONObject(i)
                db.relapseDao().insert(
                    RelapseEventEntity(
                        occurredAtMillis = r.getLong("occurredAtMillis"),
                        cigarettes = r.optInt("cigarettes", 1),
                        cause = r.optString("cause"),
                        notes = r.optString("notes"),
                        nextPlan = r.optString("nextPlan")
                    )
                )
            }

            val badges = root.optJSONArray("badges") ?: JSONArray()
            for (i in 0 until badges.length()) {
                val b = badges.getJSONObject(i)
                db.badgeDao().unlock(
                    BadgeEntity(
                        id = b.getString("id"),
                        unlockedAtMillis = b.getLong("unlockedAtMillis")
                    )
                )
            }

            val routines = root.optJSONArray("routines") ?: JSONArray()
            for (i in 0 until routines.length()) {
                val r = routines.getJSONObject(i)
                db.alternativeRoutineDao().upsert(
                    AlternativeRoutineEntity(
                        situation = r.getString("situation"),
                        oldPattern = r.optString("oldPattern"),
                        stepsCsv = r.optString("stepsCsv"),
                        updatedAtMillis = r.optLong("updatedAtMillis", System.currentTimeMillis())
                    )
                )
            }
        }
    }
}
