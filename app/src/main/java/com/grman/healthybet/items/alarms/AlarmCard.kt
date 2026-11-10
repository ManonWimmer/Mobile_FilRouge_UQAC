package com.grman.healthybet.items.alarms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grman.healthybet.R
import com.grman.healthybet.alarm.AlarmScheduler.nextTriggerMillis
import com.grman.healthybet.data.alarms.AlarmData
import com.grman.healthybet.data.alarms.AlarmResult
import com.grman.healthybet.data.alarms.formatDuration
import com.grman.healthybet.data.alarms.objectiveText
import com.grman.healthybet.ui.theme.FieldBg
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.LittleRoundedCornerShape
import com.grman.healthybet.ui.theme.MediumRoundedCornerShape
import com.grman.healthybet.ui.theme.RedFail
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun AlarmCard(
    alarm: AlarmData,
    result: AlarmResult?, // null = sonnerie pas encore déclenchée
    modifier: Modifier = Modifier,
    owner: AlarmOwner? = null,
    group: AlarmGroup? = null,
    participantEmojis: String = "",
    participantCount: Int? = null,
    onClick: (() -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null
) {
    // Statut calculé à partir de mon résultat
    val failed = result != null && result.finished && !result.success
    val statusColor = if (failed) RedFail else GreenAccent
    val statusText = when {
        result == null -> stringResource(R.string.alarm_none)
        !result.finished -> stringResource(R.string.alarm_in_progress)
        result.success -> stringResource(R.string.alarm_success)
        else -> stringResource(R.string.alarm_fail)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(FieldBg, MediumRoundedCornerShape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(14.dp)
    ) {
        if (group != null) {
            Text(
                text = stringResource(R.string.group_label, group.emoji, group.name),
                color = GreenAccent,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (owner != null) {
            OwnerRow(owner)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Heure + badge de statut
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.alarm_time, alarm.hour, alarm.minute),
                color = Color.White,
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            StatusBadge(statusText, statusColor)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = objectiveText(LocalContext.current, alarm.stepGoal, alarm.durationMinutes),
            color = GraySubtitle,
            style = MaterialTheme.typography.bodyMedium
        )

        if (result != null && alarm.stepGoal > 0) {
            AlarmProgress(result.steps, alarm.stepGoal, statusColor)
        }

        if (result != null && result.finished) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.points_earned, result.points),
                color = statusColor,
                style = MaterialTheme.typography.labelMedium
            )
        }

        if (alarm.durationMinutes > 0) {
            Spacer(modifier = Modifier.height(6.dp))
            AlarmTimeInfo(alarm, result)
        }

        if (participantCount != null) {
            ParticipantsRow(participantEmojis, participantCount)
        }

        if (actions != null) {
            Spacer(modifier = Modifier.height(14.dp))
            actions()
        }
    }
}

@Composable
private fun OwnerRow(owner: AlarmOwner) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(32.dp).background(Color.White.copy(alpha = 0.08f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = owner.emoji, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = owner.name, color = Color.White, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun StatusBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), LittleRoundedCornerShape)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center
        )
    }
}

// Pas faits / objectif + barre de progression
@Composable
private fun AlarmProgress(steps: Int, stepGoal: Int, color: Color) {
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = stringResource(R.string.steps_progress, steps, stepGoal),
        color = Color.White,
        style = MaterialTheme.typography.bodySmall
    )
    Spacer(modifier = Modifier.height(6.dp))
    LinearProgressIndicator(
        progress = { (steps / stepGoal.toFloat()).coerceIn(0f, 1f) },
        modifier = Modifier.fillMaxWidth(),
        color = color,
        trackColor = Color.White.copy(alpha = 0.1f)
    )
}

// Compte à rebours (avant la sonnerie / avant la fin) + heure de fin
@Composable
private fun AlarmTimeInfo(alarm: AlarmData, result: AlarmResult?) {
    val context = LocalContext.current
    val waiting = result == null
    val inProgress = result != null && !result.finished

    // Horloge locale : tourne seulement tant que le compte à rebours est utile
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(waiting, inProgress) {
        while (waiting || inProgress) {
            now = System.currentTimeMillis()
            delay(15_000)
        }
    }

    // Début de la fenêtre : prochaine sonnerie, ou sonnerie déjà déclenchée
    val windowStart = if (result != null) result.startedAt else alarm.nextTriggerMillis(now)
    val endMillis = windowStart + alarm.durationMinutes * 60_000L

    // Minutes restantes (arrondi au-dessus)
    val minutesLeft = when {
        waiting -> (alarm.nextTriggerMillis(now) - now + 59_999L) / 60_000L
        inProgress -> (endMillis - now + 59_999L) / 60_000L
        else -> 0L
    }.coerceAtLeast(0L).toInt()

    val endText = SimpleDateFormat("HH:mm", LocalLocale.current.platformLocale).format(Date(endMillis))

    Column {
        if (waiting || inProgress) {
            Text(
                text = stringResource(
                    if (waiting) R.string.alarm_rings_in else R.string.alarm_time_left,
                    formatDuration(context, minutesLeft)
                ),
                color = GreenAccent,
                style = MaterialTheme.typography.labelMedium
            )
        }
        Text(
            text = stringResource(R.string.alarm_ends_at, endText),
            color = GraySubtitle,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ParticipantsRow(emojis: String, count: Int) {
    Spacer(modifier = Modifier.height(12.dp))
    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
    Spacer(modifier = Modifier.height(12.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = emojis, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.registered_count, count),
            color = GraySubtitle,
            style = MaterialTheme.typography.bodySmall
        )
    }
}