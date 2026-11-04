package com.grman.healthybet.data.alarms

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.grman.healthybet.data.health.HealthDebug

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

    // Met à jour les pas, statut et si fini
    fun saveProgress(alarmId: String, uid: String, steps: Int, success: Boolean, finished: Boolean) {
        HealthDebug.debug("saveProgress alarm=$alarmId steps=$steps success=$success finished=$finished")

        collection.document(alarmId).update(
            mapOf(
                "results.$uid.steps" to steps,
                "results.$uid.success" to success,
                "results.$uid.finished" to finished
            )
        )
            .addOnSuccessListener { HealthDebug.debug("saveProgress OK") }
            .addOnFailureListener { HealthDebug.error("saveProgress failed", it) }
    }
}