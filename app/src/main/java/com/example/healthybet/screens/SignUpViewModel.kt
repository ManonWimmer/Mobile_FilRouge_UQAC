package com.grman.healthybet.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class SignUpViewModel : ViewModel() {
    var pseudo by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private val auth = FirebaseAuth.getInstance()

    fun signUp(onSuccess: () -> Unit) {

        // Validation locale des erreurs dans mdp etc
        when {
            pseudo.isBlank() -> { errorMessage = "Choisis un pseudo"; return }
            email.isBlank() -> { errorMessage = "Entre ton adresse email"; return }
            password.length < 6 -> { errorMessage = "Le mot de passe doit faire au moins 6 caractères"; return }
            password != confirmPassword -> { errorMessage = "Les mots de passe ne correspondent pas"; return }
        }

        // Debut du chargement avec spinner dans sign up screen
        isLoading = true
        errorMessage = null

        // Creation du compte dans firebase email / password
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result -> // succes : on envoie le pseudo
                val profile = UserProfileChangeRequest.Builder().setDisplayName(pseudo.trim()).build() // name
                result.user?.updateProfile(profile) // update name
                    ?.addOnCompleteListener { // on complete
                        isLoading = false
                        onSuccess()
                    }
                    ?: run { isLoading = false; onSuccess() } // si result.user null on termine quand meme sans update profile (maybe à gérer  plus tard)
            }
            .addOnFailureListener { e -> // fail
                isLoading = false
                errorMessage = e.message ?: "Erreur lors de l'inscription"
            }
    }
}