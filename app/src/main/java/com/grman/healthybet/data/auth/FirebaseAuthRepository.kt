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
    override suspend fun signIn(email: String, password: String) {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
        } catch (e: FirebaseAuthInvalidUserException) {
            throw Exception("Email ou mot de passe incorrect")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw Exception("Email ou mot de passe incorrect")
        } catch (e: FirebaseNetworkException) {
            throw Exception("Problème de connexion réseau")
        }
    }

    // Mot de passe oublié
    override suspend fun sendPasswordReset(email: String) {
        try {
            auth.sendPasswordResetEmail(email).await()
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw Exception("Adresse email invalide")
        } catch (e: FirebaseNetworkException) {
            throw Exception("Problème de connexion réseau")
        }
    }

    //  Inscription
    override suspend fun signUp(email: String, password: String, pseudo: String, emoji: String) {
        try {
            // Création du compte email / password
            val user = auth.createUserWithEmailAndPassword(email, password).await().user
                ?: error("Création du compte impossible")

            // Pseudo dans Firebase Auth
            val profile = UserProfileChangeRequest.Builder().setDisplayName(pseudo).build()
            user.updateProfile(profile).await()

            // Membre dans Firestore : id + pseudo + emoji
            val member = hashMapOf(
                "id" to user.uid,
                "username" to pseudo,
                "emoji" to emoji.ifBlank { "😄" }
            )
            db.collection("members").document(user.uid).set(member).await()
        } catch (e: FirebaseAuthUserCollisionException) {
            throw Exception("Cet email est déjà utilisé")
        } catch (e: FirebaseAuthWeakPasswordException) {
            throw Exception("Mot de passe trop faible")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw Exception("Adresse email invalide")
        } catch (e: FirebaseNetworkException) {
            throw Exception("Problème de connexion réseau")
        }
    }
}