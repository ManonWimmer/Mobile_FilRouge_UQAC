package com.grman.healthybet.data.auth

interface AuthRepository {
    suspend fun signIn(email: String, password: String)
    suspend fun sendPasswordReset(email: String)
    suspend fun signUp(email: String, password: String, pseudo: String, emoji: String)
}