package com.grman.healthybet.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.grman.healthybet.data.Group

class GroupsViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance();
    private val uid: String? get() = auth.currentUser?.uid

    val myGroups = mutableStateListOf<Group>()
    val discoverGroups = mutableStateListOf<Group>()

    var searchQuery by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Recherche: filtre simplement sur le nom (un texte vide garde tous les groupes)
    val filteredDiscover: List<Group>
        get() = discoverGroups.filter { it.name.contains(searchQuery.trim(), ignoreCase = true) }


    // Charge les groupes depuis Firestore et les range dans myGroups ou discoverGroups
    fun loadGroups() {
        // Check id
        val uid = auth.currentUser?.uid
        if (uid == null) {
            errorMessage = "Utilisateur non connecté"
            return
        }

        db.collection("groups").get()
            .addOnSuccessListener { result ->
                myGroups.clear()
                discoverGroups.clear()

                for (group in result.toObjects(Group::class.java)) {
                    // User id -> my groups
                    if (uid in group.members) {
                        myGroups.add(group)
                    } else { // Else -> discover groups
                        discoverGroups.add(group)
                    }
                }
            }
            .addOnFailureListener { e ->
                errorMessage = e.message ?: "Erreur de chargement des groupes"
            }
    }


    fun createGroup(name: String, emoji: String, description: String, onSuccess: () -> Unit) {
        if (name.isBlank()) {
            errorMessage = "Le nom du groupe est vide"
            return
        }

        val uid = auth.currentUser?.uid
        if (uid == null) {
            errorMessage = "Utilisateur non connecté"
            return
        }

        isLoading = true
        errorMessage = null

        // New groupe avec 1 membre le user
        val group = hashMapOf(
            "name" to name.trim(),
            "emoji" to emoji,
            "description" to description.trim(),
            "ownerId" to uid,
            "members" to listOf(uid)
        )

        db.collection("groups").add(group)
            .addOnSuccessListener {
                isLoading = false
                loadGroups()
                onSuccess()
            }
            .addOnFailureListener { e ->
                isLoading = false
                errorMessage = e.message ?: "Erreur de création du groupe"
            }
    }

    fun joinGroup(groupId: String) {
        // Check uid
        val uid = auth.currentUser?.uid ?: return

        // Add id to members from group
        db.collection("groups").document(groupId)
            .update("members", FieldValue.arrayUnion(uid))
            .addOnSuccessListener { loadGroups() }
            .addOnFailureListener { e ->
                errorMessage = e.message ?: "Impossible de rejoindre le groupe"
            }
    }
}