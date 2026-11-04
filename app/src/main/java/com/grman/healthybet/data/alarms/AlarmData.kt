package com.grman.healthybet.data.alarms

import com.google.firebase.firestore.Exclude
import kotlin.math.roundToInt

data class AlarmData(
    val id: String = "",
    val ownerId: String = "",
    val groupId: String = "",
    val participantIds: List<String> = emptyList(), // uid des inscrits (créateur inclus)

    val hour: Int = 0,
    val minute: Int = 0,

    //val objective: String = "", // plus besoin avec seulements les steps + utilisait pas la loca
    val advancement: String = AlarmState.NONE.name,

    val stepGoal: Int = 0, // pas
    val durationMinutes: Int = 0, // temps

    val success: Boolean = false,
    val enabled: Boolean = true
) {
    @get:Exclude
    val state: AlarmState
        get() = runCatching { AlarmState.valueOf(advancement) }.getOrDefault(AlarmState.NONE)
}

enum class AlarmState{
    NONE,
    IN_PROGRESS,
    DONE
}

public fun GetObjectiveText(stepGoal: Int, durationMinutes: Int): String
{
    if (stepGoal > 0 && durationMinutes > 0)
        return "👟 %,d pas en %s".format(java.util.Locale.FRANCE, RoundStepGoal(stepGoal), FormatDuration(durationMinutes)) // todo: strings.xml
    else
        return ""
}

fun FormatDuration(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return when {
        h == 0 -> "${m}min"
        m == 0 -> "${h}h"
        else -> "$h h %02d".format(m)
    }
}

public fun RoundStepGoal(stepGoal: Int): Int {
    return (stepGoal / 500f).roundToInt() * 500;
}

