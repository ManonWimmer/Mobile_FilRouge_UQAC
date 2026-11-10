package com.grman.healthybet.data.health

import android.content.Context
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant

object StepsReader {
    private const val TAG = "StepsReader"

    // Permission
    val permissions = setOf(HealthPermission.getReadPermission(StepsRecord::class))

    // Check si health connect installé et utilisable
    fun isAvailable(context: Context): Boolean {
        val status = HealthConnectClient.getSdkStatus(context) // 3 = dispo, 2 = maj requise, 1 = absent
        HealthDebug.debug("SDK status = $status")
        return status == HealthConnectClient.SDK_AVAILABLE
    }

    // Pas entre from et to (ms), ou null si indisponible / permission refusée.
    suspend fun readSteps(context: Context, from: Long, to: Long): Long? {
        HealthDebug.debug("readSteps from=${Instant.ofEpochMilli(from)} to=${Instant.ofEpochMilli(to)}")

        // Pas de health connect
        if (!isAvailable(context)) {
            HealthDebug.debug("readSteps -> null (Health Connect indisponible)")
            return null
        }

        if (to <= from) {
            HealthDebug.debug("readSteps -> null (fenêtre vide : to <= from)")
            return null
        }

        return try {
            val client = HealthConnectClient.getOrCreate(context)

            // Check permission
            val granted = client.permissionController.getGrantedPermissions()
            if (!granted.containsAll(permissions)) {
                HealthDebug.debug("readSteps -> null (permission refusée, accordées = $granted)")
                return null
            }

            // aggregate = total des pas sur la période
            val total = client.aggregate(
                AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(
                        Instant.ofEpochMilli(from), Instant.ofEpochMilli(to)
                    )
                )
            )[StepsRecord.COUNT_TOTAL] ?: 0L

            HealthDebug.debug("readSteps -> $total pas")
            total
        } catch (e: Exception) {
            HealthDebug.error("readSteps failed", e)
            null
        }
    }

    // Texte de debug : pas depuis minuit + pas des 10 dernières minutes
    suspend fun debugSnapshot(context: Context): String {
        val now = System.currentTimeMillis()

        // Minuit de ce matin
        val startOfDay = java.time.LocalDate.now()
            .atStartOfDay(java.time.ZoneId.systemDefault())
            .toInstant().toEpochMilli()

        val today = readSteps(context, startOfDay, now)
        val last10 = readSteps(context, now - 10 * 60_000L, now)

        val text = "Pas aujourd'hui : $today | 10 dernières min : $last10"
        HealthDebug.debug(text)
        return text
    }
}