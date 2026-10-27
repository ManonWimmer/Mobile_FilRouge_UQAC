package com.grman.healthybet.data.groups

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseGroupRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : GroupRepository {

    private fun uid(): String =
        auth.currentUser?.uid ?: error("Utilisateur non connecté")

    override suspend fun getGroups(): GroupsOverview {
        val uid = uid()
        val groups = db.collection("groups").get().await().toObjects(Group::class.java)
        val (mine, discover) = groups.partition { uid in it.members }
        return GroupsOverview(mine, discover)
    }

    override suspend fun createGroup(name: String, emoji: String, description: String) {
        val uid = uid()
        val group = hashMapOf(
            "name" to name,
            "emoji" to emoji,
            "description" to description,
            "ownerId" to uid,
            "members" to listOf(uid)
        )
        db.collection("groups").add(group).await()
    }

    override suspend fun joinGroup(groupId: String) {
        db.collection("groups").document(groupId)
            .update("members", FieldValue.arrayUnion(uid()))
            .await()
    }

    override suspend fun leaveGroup(groupId: String) {
        db.collection("groups").document(groupId)
            .update("members", FieldValue.arrayRemove(uid()))
            .await()
    }
}