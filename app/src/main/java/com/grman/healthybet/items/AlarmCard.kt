package com.grman.healthybet.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grman.healthybet.R
import com.grman.healthybet.data.alarms.AlarmData
import com.grman.healthybet.data.alarms.AlarmResult
import com.grman.healthybet.data.alarms.objectiveText
import com.grman.healthybet.ui.theme.FieldBg
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.LittleRoundedCornerShape
import com.grman.healthybet.ui.theme.MediumRoundedCornerShape
import com.grman.healthybet.ui.theme.RedFail

// Créateur de l'alarme (affiché seulement si fourni)
data class AlarmOwner(val emoji: String, val name: String)

@Composable
fun AlarmCard(
    alarm: AlarmData,
    result: AlarmResult?, // (null = sonnerie pas encore déclenchée)
    modifier: Modifier = Modifier,
    owner: AlarmOwner? = null,
    participantEmojis: String = "",
    participantCount: Int? = null,
    onClick: (() -> Unit)? = null,
    actions: (@Composable ColumnScope.() -> Unit)? = null
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

        // Créateur (optionnel)
        if (owner != null) {
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

            Box(
                modifier = Modifier
                    .background(statusColor.copy(alpha = 0.15f), LittleRoundedCornerShape)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Objectif
        Text(
            text = objectiveText(LocalContext.current, alarm.stepGoal, alarm.durationMinutes),
            color = GraySubtitle,
            style = MaterialTheme.typography.bodyMedium
        )

        // Pas depuis la sonnerie / objectif
        if (result != null && alarm.stepGoal > 0) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.steps_progress, result.steps, alarm.stepGoal),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { (result.steps / alarm.stepGoal.toFloat()).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = statusColor,
                trackColor = Color.White.copy(alpha = 0.1f)
            )
        }

        // Points gagnés une fois l'alarme terminée
        if (result != null && result.finished) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.points_earned, result.points),
                color = statusColor,
                style = MaterialTheme.typography.labelMedium
            )
        }

        // Inscrits (optionnel)
        if (participantCount != null) {
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = participantEmojis, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.registered_count, participantCount),
                    color = GraySubtitle,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Boutons (optionnels)
        if (actions != null) {
            Spacer(modifier = Modifier.height(14.dp))
            actions()
        }
    }
}