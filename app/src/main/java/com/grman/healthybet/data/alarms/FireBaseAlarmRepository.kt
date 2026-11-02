package com.grman.healthybet.data.alarms

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

object AlarmRepository {
    private const val TAG = "AlarmRepository"

    private val collection: CollectionReference
        get() = FirebaseFirestore.getInstance().collection("alarms")

    fun create(alarm: AlarmData, onDone: (AlarmData?) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return onDone(null)
        val doc = collection.document()
        val withId = alarm.copy(id = doc.id, ownerId = uid)
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
            .whereEqualTo("ownerId", userId)
            .addSnapshotListener { snap, error ->
                if (error != null) {
                    Log.e(TAG, "Échec observeUserAlarms", error)
                    return@addSnapshotListener
                }
                onResult(snap?.toObjects(AlarmData::class.java).orEmpty())
            }
    }

    /* Lecture unique des alarmes active d'un utilisateur (pour les programmer) */
    fun getEnabledAlarms(userId: String, onResult: (List<AlarmData>) -> Unit) {
        collection
            .whereEqualTo("ownerId", userId)
            .whereEqualTo("enabled", true)
            .get()
            .addOnSuccessListener { onResult(it.toObjects(AlarmData::class.java)) }
            .addOnFailureListener {
                Log.e(TAG, "Échec getEnabledAlarms", it)
                onResult(emptyList())
            }
    }
}