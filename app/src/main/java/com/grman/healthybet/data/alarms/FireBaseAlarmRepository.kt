package com.grman.healthybet.data.alarms

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.grman.healthybet.data.health.HealthDebug
import kotlin.text.get

object AlarmRepository {
    private const val TAG = "AlarmRepository"

    private val collection: CollectionReference
        get() = FirebaseFirestore.getInstance().collection("alarms")

    fun create(alarm: AlarmData, onDone: (AlarmData?) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return onDone(null)
        val doc = collection.document()
        val withId = alarm.copy(id = doc.id, ownerId = uid, participantIds = listOf(uid))
        doc.set(withId)
            .addOnFailureListener { Log.e(TAG, "Échec de la sauvegarde", it) }
        onDone(withId)
    }

    fun setEnabled(id: String, enabled: Boolean) {
        collection.document(id).update("enabled", enabled)
            .addOnFailureListener { Log.e(TAG, "Échec setEnabled", it) }
    }

    fun setAdvancement(id: String, state: AlarmState, success: Boolean) {
        collection.document(id).update(
            mapOf("advancement" to state.name, "success" to success)
        ).addOnFailureListener { Log.e(TAG, "Échec setAdvancement", it) }
    }

    fun delete(id: String) {
        collection.document(id).delete()
            .addOnFailureListener { Log.e(TAG, "Échec delete", it) }
    }

    fun observeUserAlarms(userId: String, onResult: (List<AlarmData>) -> Unit): ListenerRegistration {
        return collection
            .whereArrayContains("participantIds", userId)
            .addSnapshotListener { snap, error ->
                if (error != null) {
                    Log.e(TAG, "Échec observeUserAlarms", error)
                    return@addSnapshotListener
                }
                onResult(snap?.toObjects(AlarmData::class.java).orEmpty())
            }
    }

    fun observeGroupAlarms(groupId: String, onResult: (List<AlarmData>) -> Unit): ListenerRegistration {
        return collection
            .whereEqualTo("groupId", groupId)
            .addSnapshotListener { snap, error ->
                if (error != null) {
                    Log.e(TAG, "Échec observeGroupAlarms", error)
                    return@addSnapshotListener
                }
                onResult(snap?.toObjects(AlarmData::class.java).orEmpty())
            }
    }

    /* Lecture unique des alarmes active d'un utilisateur (pour les programmer) */
    fun getEnabledAlarms(userId: String, onResult: (List<AlarmData>) -> Unit) {
        collection
            .whereArrayContains("participantIds", userId)
            .whereEqualTo("enabled", true)
            .get()
            .addOnSuccessListener { onResult(it.toObjects(AlarmData::class.java)) }
            .addOnFailureListener {
                Log.e(TAG, "Échec getEnabledAlarms", it)
                onResult(emptyList())
            }
    }

    fun join(alarmId: String, uid: String) {
        collection.document(alarmId).update("participantIds", FieldValue.arrayUnion(uid))
            .addOnFailureListener { Log.e(TAG, "Échec join", it) }
    }

    fun leave(alarmId: String, uid: String) {
        collection.document(alarmId).update("participantIds", FieldValue.arrayRemove(uid))
            .addOnFailureListener { Log.e(TAG, "Échec leave", it) }
    }

    /*
    fun onAlarmStopped(alarmId: String, uid: String?) {
        if (uid == null) return
        leave(alarmId, uid);
    }
    */

    // Appelé quand la sonnerie démarre : enregistre l'heure de départ du suivi
    fun startTracking(alarmId: String, uid: String, startedAt: Long) {
        HealthDebug.debug("startTracking alarm=$alarmId uid=$uid startedAt=$startedAt")

        collection.document(alarmId).update("results.$uid", AlarmResult(startedAt = startedAt))
            .addOnSuccessListener { HealthDebug.debug("startTracking OK") }
            .addOnFailureListener { HealthDebug.error("startTracking failed", it) }
    }

    private val db get() = FirebaseFirestore.getInstance()

    // Met à jour les pas, statut et si fini
    fun saveProgress(alarmId: String, groupId: String, uid: String, steps: Int, stepGoal: Int, success: Boolean, finished: Boolean) {
        HealthDebug.debug("saveProgress alarm=$alarmId steps=$steps success=$success finished=$finished")

        val alarmRef = collection.document(alarmId)
        val groupRef = db.collection("groups").document(groupId)

        // Update points, steps, finished et sucess (run transaction au cas ou save progress est appelé 2 fois la meme frame)
        db.runTransaction { transaction ->

            // Déjà terminé on save rien
            if (transaction.get(alarmRef).getBoolean("results.$uid.finished") == true)
                return@runTransaction null

            val points = if (finished && success) stepGoal else 0 // 1 pas d'objectif = 1 point, seulement si réussi

            transaction.update(alarmRef, mapOf(
                "results.$uid.steps" to steps,
                "results.$uid.success" to success,
                "results.$uid.finished" to finished,
                "results.$uid.points" to points
            ))

            // increment des points du user id dans le groupe avec ceux qu'il viennent de gagner avec cette alarme
            if (points > 0 && groupId.isNotBlank()) {
                transaction.update(groupRef, "points.$uid", FieldValue.increment(points.toLong()))
            }
            null
        }
            .addOnSuccessListener { HealthDebug.debug("saveProgress OK") }
            .addOnFailureListener { HealthDebug.error("saveProgress failed", it) }
    }

    // Alarmes de tous les groupes de l'user (max 30)
    fun observeAlarmsOfGroups(groupIds: List<String>, onResult: (List<AlarmData>) -> Unit): ListenerRegistration =
        collection.whereIn("groupId", groupIds.take(30))
            .addSnapshotListener { snap, error ->
                if (error != null) {
                    Log.e(TAG, "Failed to observe groups alarms", error)
                    return@addSnapshotListener
                }
                onResult(snap?.toObjects(AlarmData::class.java).orEmpty())
            }

    fun observeUserStats(
        userId: String,
        onResult: (points: Int, successfulAlarms: Int) -> Unit
    ): ListenerRegistration {
        return observeUserAlarms(userId) { alarms ->
            val results = alarms.mapNotNull { it.results[userId] }

            val points = results.sumOf { it.points }
            val successfulAlarms = results.count {
                it.finished && it.success
            }

            onResult(points, successfulAlarms)
        }
    }
}