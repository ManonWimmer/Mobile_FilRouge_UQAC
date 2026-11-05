package com.grman.healthybet.data.health

import android.content.Context
import com.grman.healthybet.data.alarms.AlarmData
import com.grman.healthybet.data.alarms.AlarmRepository

// lit les pas depuis le démarrage de la sonnerie et enregistre le résultat dans Firestore
suspend fun refreshProgress(context: Context, alarm: AlarmData, uid: String) {

    // pas encore sonné ou terminé
    val result = alarm.results[uid] ?: return
    if (result.finished)
        return

    // Fenêtre de comptage : de la sonnerie jusqu'à (sonnerie + durée)
    val end = result.startedAt + alarm.durationMinutes * 60_000L // * 60_000L pour minutes en millisecondes
    val now = System.currentTimeMillis()
    val expired = now >= end
    val remainingMin = (end - now) / 60_000L
    HealthDebug.debug("refresh[${alarm.id}] objectif=${alarm.stepGoal} pas, reste ${remainingMin} min")

    // Pas réalisés dans la fenêtre de temps
    val steps = StepsReader.readSteps(context, result.startedAt, minOf(now, end))?.toInt()
    if (steps == null) {
        HealthDebug.debug("refresh[${alarm.id}] lecture impossible, on réessaiera")
        return
    }

    val success = steps >= alarm.stepGoal // objectif atteint
    val finished = success || expired // terminé si réussi ou si le temps est écoulé

    HealthDebug.debug("refresh[${alarm.id}] steps=$steps/${alarm.stepGoal} success=$success finished=$finished")


    // On n'écrit dans Firestore que si quelque chose a changé
    if (steps != result.steps || finished) {
        AlarmRepository.saveProgress(alarm.id, alarm.groupId, uid, steps, alarm.stepGoal, success, finished)
    } else {
        HealthDebug.debug("refresh[${alarm.id}] aucun changement, pas d'écriture")
    }
}