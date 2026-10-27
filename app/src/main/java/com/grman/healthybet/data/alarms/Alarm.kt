package com.grman.healthybet.data.alarms

data class Alarm(
    val time: String,
    val status: String,
    val success : Boolean,
    val finished : Boolean,
    val objective: String,
    val reward: String
)