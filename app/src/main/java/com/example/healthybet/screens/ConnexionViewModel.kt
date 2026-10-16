package com.grman.healthybet.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class ConnexionViewModel() : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private val auth = FirebaseAuth.getInstance()

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
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { // succes
                isLoading = false
                onSuccess()
            }
            .addOnFailureListener { e -> // fail
                isLoading = false
                errorMessage = e.message ?: "Erreur de connexion"
            }
    }

    // todo: mdp oublié pour reset password (auth.sendPasswordResetEmail(email))
}