package com.grman.healthybet.data.auth

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun sendPasswordReset(email: String): Result<Unit>
}