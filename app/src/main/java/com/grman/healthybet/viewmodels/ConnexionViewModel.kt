package com.grman.healthybet.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.grman.healthybet.data.auth.AuthRepository
import com.grman.healthybet.data.auth.FirebaseAuthRepository
import kotlinx.coroutines.launch

class ConnexionViewModel(private val authRepository: AuthRepository = FirebaseAuthRepository()) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun signIn(onSuccess: () -> Unit) {

        // Validation location de email et mdp
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "L'email et/ou le mot de passe sont vides"
            return
        }

        // Debut du chargement avec spinner dans connexion screen
        isLoading = true
        errorMessage = null

        // Connexion au compte dans firebase email / password
        viewModelScope.launch {
            authRepository.signIn(email.trim(), password)
                .onSuccess { onSuccess() }
                .onFailure { errorMessage = it.message ?: "Erreur de connexion" }
            isLoading = false
        }
    }

    fun resetPassword() {
        if (email.isBlank()) {
            errorMessage = "Entre ton email pour réinitialiser le mot de passe"
            return
        }
        viewModelScope.launch {
            authRepository.sendPasswordReset(email.trim())
                .onFailure { errorMessage = it.message }
        }
    }
}