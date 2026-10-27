package com.grman.healthybet.data

data class Alarm(
    val time: String,
    val status: String,
    val success : Boolean,
    val finished : Boolean,
    val objective: String,
    val reward: String
)