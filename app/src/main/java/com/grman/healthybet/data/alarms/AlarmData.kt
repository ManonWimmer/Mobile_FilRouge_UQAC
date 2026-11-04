package com.grman.healthybet.data.alarms

import com.google.firebase.firestore.Exclude

data class AlarmData(
    val id: String = "",
    val ownerId: String = "",
    val groupId: String = "",
    val participantIds: List<String> = emptyList(), // uid des inscrits (créateur inclus)

    val hour: Int = 0,
    val minute: Int = 0,

    val objective: String = "",
    val advancement: String = AlarmState.NONE.name,

    val success: Boolean = false,
    val enabled: Boolean = true
) {
    @get:Exclude
    val state: AlarmState
        get() = runCatching { AlarmState.valueOf(advancement) }.getOrDefault(AlarmState.NONE)
}

enum class AlarmState{
    NONE,
    EN_COURS,
    FINIT
}

