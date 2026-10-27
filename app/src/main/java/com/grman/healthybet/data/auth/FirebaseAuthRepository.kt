package com.grman.healthybet.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class FirebaseAuthRepository(private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
                             private val db: FirebaseFirestore = FirebaseFirestore.getInstance()): AuthRepository {

    // Connexion
    override suspend fun signIn(email: String, password: String): Result<Unit> =
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception(e.toSignInMessage(), e))
        }

    override suspend fun sendPasswordReset(email: String): Result<Unit> =
        try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception(e.toSignInMessage(), e))
        }

    private fun Exception.toSignInMessage(): String = when (this) {
        is FirebaseAuthInvalidUserException,
        is FirebaseAuthInvalidCredentialsException -> "Email ou mot de passe incorrect"
        is FirebaseNetworkException -> "Problème de connexion réseau"
        else -> message ?: "Erreur de connexion"
    }

    // Sign up
    override suspend fun signUp(email: String, password: String, pseudo: String, emoji: String): Result<Unit> {
        return try {
            // Création du compte email / password
            val user = auth.createUserWithEmailAndPassword(email, password).await().user
                ?: return Result.success(Unit) // user null : on termine quand même (à gérer plus tard)

            // Pseudo dans Firebase Auth
            val profile = UserProfileChangeRequest.Builder()
                .setDisplayName(pseudo)
                .build()
            user.updateProfile(profile).await()

            // Membre dans Firestore : id + pseudo + emoji
            val member = hashMapOf(
                "id" to user.uid,
                "username" to pseudo,
                "emoji" to emoji.ifBlank { "😄" }
            )

            db.collection("members").document(user.uid).set(member).await()

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception(e.toSignUpMessage(), e))
        }
    }

    private fun Exception.toSignUpMessage(): String = when (this) {
        is FirebaseAuthUserCollisionException -> "Cet email est déjà utilisé"
        is FirebaseAuthWeakPasswordException -> "Mot de passe trop faible"
        is FirebaseAuthInvalidCredentialsException -> "Adresse email invalide"
        is FirebaseNetworkException -> "Problème de connexion réseau"
        else -> message ?: "Erreur lors de l'inscription"
    }
}