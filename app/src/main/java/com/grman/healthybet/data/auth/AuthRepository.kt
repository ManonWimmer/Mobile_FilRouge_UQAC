package com.grman.healthybet.data.auth

interface AuthRepository {

    // Connexion
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun sendPasswordReset(email: String): Result<Unit>

    // Sign up
    suspend fun signUp(email: String, password: String, pseudo: String, emoji: String): Result<Unit>
}