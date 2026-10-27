package com.grman.healthybet.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.grman.healthybet.data.auth.AuthRepository
import com.grman.healthybet.data.auth.FirebaseAuthRepository
import kotlinx.coroutines.launch

class SignUpViewModel(private val authRepository: AuthRepository = FirebaseAuthRepository()) : ViewModel() {
    var pseudo by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var emoji by mutableStateOf("😀") // meme que le 1er de la liste dans sign up screen
    var confirmPassword by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)


    fun signUp(onSuccess: () -> Unit) {

        // Validation locale des erreurs dans mdp etc
        when {
            pseudo.isBlank() -> { errorMessage = "Choisis un pseudo"; return }
            pseudo.length < 3 -> { errorMessage = "Le pseudo doit faire au moins 3 caractères"; return }
            email.isBlank() -> { errorMessage = "Entre ton adresse email"; return }
            emoji.isBlank() -> { errorMessage = "Sélectionne un emoji"; return }
            password.length < 6 -> { errorMessage = "Le mot de passe doit faire au moins 6 caractères"; return }
            password != confirmPassword -> { errorMessage = "Les mots de passe ne correspondent pas"; return }
        }

        // Debut du chargement avec spinner dans sign up screen
        isLoading = true
        errorMessage = null

        // Creation du compte dans firebase email / password
        viewModelScope.launch {
            try {
                authRepository.signUp(email.trim(), password, pseudo.trim(), emoji)
                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Erreur lors de l'inscription"
            }
            isLoading = false
        }
    }
}