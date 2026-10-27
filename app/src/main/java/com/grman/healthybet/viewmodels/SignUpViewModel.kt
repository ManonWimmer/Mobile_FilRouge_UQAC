package com.grman.healthybet.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class SignUpViewModel : ViewModel() {
    var pseudo by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var emoji by mutableStateOf("😀") // meme que le 1er de la liste dans sign up screen
    var confirmPassword by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    fun signUp(onSuccess: () -> Unit) {

        // Validation locale des erreurs dans mdp etc
        when {
            pseudo.isBlank() -> { errorMessage = "Choisis un pseudo"; return }
            email.isBlank() -> { errorMessage = "Entre ton adresse email"; return }
            emoji.isBlank() -> { errorMessage = "Sélectionne un emoji"; return }
            password.length < 6 -> { errorMessage = "Le mot de passe doit faire au moins 6 caractères"; return }
            password != confirmPassword -> { errorMessage = "Les mots de passe ne correspondent pas"; return }
        }

        // Debut du chargement avec spinner dans sign up screen
        isLoading = true
        errorMessage = null

        // Creation du compte dans firebase email / password
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result -> // succes : on envoie le pseudo
                val user = result.user

                // si user null on termine quand meme (maybe à gérer plus tard)
                if (user == null) {
                    isLoading = false
                    onSuccess()
                    return@addOnSuccessListener
                }

                // Pseudo dans frebase authentificator
                val profile = UserProfileChangeRequest.Builder().setDisplayName(pseudo.trim()).build()
                user.updateProfile(profile).addOnCompleteListener {

                    // member avec id + pseudo + emoji
                    val member = hashMapOf(
                        "id" to user.uid,
                        "username" to pseudo.trim(),
                        "emoji" to emoji.ifBlank { "😄" } // emoji par défaut si aucun choisi
                    )

                    db.collection("members").document(user.uid).set(member)
                        .addOnCompleteListener { // todo: gérer fail
                            isLoading = false
                            onSuccess()
                        }
                }
            }
            .addOnFailureListener { e -> // fail
                isLoading = false
                errorMessage = e.message ?: "Erreur lors de l'inscription"
            }
    }
}