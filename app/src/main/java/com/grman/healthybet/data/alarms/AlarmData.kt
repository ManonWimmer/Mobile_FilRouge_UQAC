package com.grman.healthybet.data.alarms

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.firebase.firestore.Exclude
import com.grman.healthybet.R
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
    val enabled: Boolean = true,

    val results: Map<String, AlarmResult> = emptyMap(), // uid -> résultat
) {
    @get:Exclude
    val state: AlarmState
        get() = runCatching { AlarmState.valueOf(advancement) }.getOrDefault(AlarmState.NONE)
}

data class AlarmResult(
    val startedAt: Long = 0L,      // ms : moment où la sonnerie a démarré
    val steps: Int = 0,
    val success: Boolean = false,
    val finished: Boolean = false  // objectif atteint ou délai écoulé
)

enum class AlarmState{
    NONE,
    IN_PROGRESS,
    DONE
}

public fun objectiveText(context: Context, stepGoal: Int, durationMinutes: Int): String =
    if (stepGoal > 0 && durationMinutes > 0)
        context.getString(
            R.string.alarm_objective,
            roundStepGoal(stepGoal),
            formatDuration(context, durationMinutes)
        )
    else
        ""

fun formatDuration(context: Context, minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return when {
        h == 0 -> context.getString(R.string.duration_minutes, m)
        m == 0 -> context.getString(R.string.duration_hours, h)
        else -> context.getString(R.string.duration_hours_minutes, h, m)
    }
}

public fun roundStepGoal(stepGoal: Int): Int {
    return (stepGoal / 500f).roundToInt() * 500;
}

