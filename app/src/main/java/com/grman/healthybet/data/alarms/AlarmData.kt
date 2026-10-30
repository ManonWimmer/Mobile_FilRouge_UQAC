package com.grman.healthybet.data.alarms

data class AlarmData(
    val id: String = "",
    val hour: Int = 0,
    val minute: Int = 0,
    val objective: String = "",
    val advancement: AlarmState = AlarmState.NONE,
    val success: Boolean = false,
    val enabled: Boolean = true
)

enum class AlarmState{
    NONE,
    EN_COURS,
    FINIT
}

