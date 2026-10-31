package com.grman.healthybet.data.alarms

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

object AlarmRepository {

    private fun collection(): CollectionReference? {
        val id = FirebaseAuth.getInstance().currentUser?.uid ?: return null
        return FirebaseFirestore.getInstance()
            .collection("alarms")
    }

    fun create(alarm: AlarmData, onDone: (AlarmData?) -> Unit) {
        val col = collection() ?: return onDone(null)
        val doc = col.document()
        val withId = alarm.copy(id = doc.id)
        doc.set(withId)
        onDone(withId)
    }

    fun setEnabled(id: String, enabled: Boolean) {
        collection()?.document(id)?.update("enabled", enabled)
    }
}