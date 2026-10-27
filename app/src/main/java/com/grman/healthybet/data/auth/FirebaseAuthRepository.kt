package com.grman.healthybet.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class FirebaseAuthRepository(private val auth: FirebaseAuth = FirebaseAuth.getInstance()): AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<Unit> =
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserMessage(), e))
        }

    override suspend fun sendPasswordReset(email: String): Result<Unit> =
        try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception(e.toUserMessage(), e))
        }

    private fun Exception.toUserMessage(): String = when (this) {
        is FirebaseAuthInvalidUserException,
        is FirebaseAuthInvalidCredentialsException -> "Email ou mot de passe incorrect"
        is FirebaseNetworkException -> "Problème de connexion réseau"
        else -> message ?: "Erreur de connexion"
    }
}