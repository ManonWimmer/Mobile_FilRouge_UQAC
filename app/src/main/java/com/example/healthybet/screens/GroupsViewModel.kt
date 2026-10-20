package com.grman.healthybet.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

data class Group(
    val id: String,
    val name: String,
    val emoji: String = "",
    val description: String = "",
    val memberCount: Int, // todo: membres donc count à recup
    val memberEmojis: List<String> = emptyList(), // todo: photos de profil des membres
    val joined: Boolean = false
)

class GroupsViewModel : ViewModel() {

    // todo : bdd
    val myGroups = mutableStateListOf(
        Group("1", "Les runners", "🏃", "", 12, listOf("😎", "😆", "😎"), joined = true),
        Group("2", "Potes UQAC", "", "", 8, listOf("😎", "🥱", "🙂"), joined = true),
        Group("3", "Hyrox 6AM Club", "", "", 15, listOf("😆", "😎", "🤔"), joined = true),
    )

    // todo: bdd
    val discoverGroups = mutableStateListOf(
        Group("10", "Lève-tôt Saguenay", "🌅", "Réveil avant 6h, on se motive entre nous", 42, memberEmojis = listOf("😎", "🥱")),
        Group("11", "Marathon 2026", "🏅", "Préparation marathon, objectifs de course", 27),
        Group("12", "CrossFit UQAC", "🏋️", "Les gars et filles du box", 19),
        Group("13", "10 000 pas/jour", "👟", "Défi quotidien de marche", 64),
    )

    var searchQuery by mutableStateOf("")

    // todo: bdd
    val filteredDiscover: List<Group>
        get() = discoverGroups.filter {
            !it.joined && (searchQuery.isBlank() || it.name.contains(searchQuery.trim(), ignoreCase = true))
        }

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private val auth = FirebaseAuth.getInstance() // pour plus tard au moins c fait

    fun createGroup(name: String, emoji: String, description: String, onSuccess: () -> Unit) {

        // Nom vide
        if (name.isBlank()) {
            errorMessage = "Le nom du groupe est vide"
            return
        }

        // User id null
        val uid = auth.currentUser?.uid
        if (uid == null) {
            errorMessage = "Utilisateur non connecté"
            return
        }

        isLoading = true
        errorMessage = null

        // todo: bdd
        myGroups.add(
            0,
            Group(
                id = System.currentTimeMillis().toString(), // id avec time millis
                name = name.trim(),
                emoji = emoji,
                description = description.trim(),
                memberCount = 1,
                memberEmojis = listOf("😄"), // todo: member profile pictures ou members id
                joined = true
            )
        )

        isLoading = false
        onSuccess()
    }

    fun joinGroup(groupId: String) {
        // todo Firestore: update(groupId, "members", FieldValue.arrayUnion(uid), "memberCount", FieldValue.increment(1))

        // Get index in discover groups
        val index = discoverGroups.indexOfFirst { it.id == groupId }

        if (index == -1)
            return

        // Get group from discover groups
        val group = discoverGroups[index].copy(joined = true)

        // Update group
        discoverGroups[index] = group
        myGroups.add(group)
    }
}