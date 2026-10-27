package com.grman.healthybet.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.grman.healthybet.data.groups.FirebaseGroupRepository
import com.grman.healthybet.data.groups.Group
import com.grman.healthybet.data.groups.GroupRepository
import kotlinx.coroutines.launch

class GroupsViewModel(private val groupRepository: GroupRepository = FirebaseGroupRepository()) : ViewModel() {

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
        viewModelScope.launch {
            try {
                refreshGroups()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Erreur de chargement des groupes"
            }
        }
    }


    fun createGroup(name: String, emoji: String, description: String, onSuccess: () -> Unit) {
        if (name.isBlank()) {
            errorMessage = "Le nom du groupe est vide"
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                groupRepository.createGroup(name.trim(), emoji, description.trim())
                refreshGroups()
                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Erreur de création du groupe"
            }
            isLoading = false
        }
    }

    fun joinGroup(groupId: String) {
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                groupRepository.joinGroup(groupId)
                refreshGroups()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Impossible de rejoindre le groupe"
            }
            isLoading = false;
        }
    }

    fun leaveGroup(groupId: String, onSuccess: () -> Unit) {
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                groupRepository.leaveGroup(groupId)
                refreshGroups()
                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Impossible de quitter le groupe"
            }
            isLoading = false
        }
    }

    private suspend fun refreshGroups() {
        val overview = groupRepository.getGroups()

        myGroups.clear()
        myGroups.addAll(overview.mine)

        discoverGroups.clear()
        discoverGroups.addAll(overview.discover)
    }
}