package com.grman.healthybet.screens.alarms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.grman.healthybet.R
import com.grman.healthybet.data.alarms.AlarmData
import com.grman.healthybet.data.alarms.AlarmRepository
import com.grman.healthybet.data.alarms.AlarmState
import com.grman.healthybet.data.alarms.objectiveText
import com.grman.healthybet.data.health.HealthDebug
import com.grman.healthybet.data.health.StepsReader
import com.grman.healthybet.data.health.refreshProgress
import com.grman.healthybet.ui.theme.FieldBg
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.HealthyBetTheme
import com.grman.healthybet.ui.theme.LittleRoundedCornerShape
import com.grman.healthybet.ui.theme.MediumRoundedCornerShape
import com.grman.healthybet.ui.theme.RedFail
import kotlinx.coroutines.delay
import kotlin.comparisons.compareBy


@Composable
fun AlarmsScreen(modifier: Modifier = Modifier, onAlarmClick: (String) -> Unit = {}) {

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val appContext = LocalContext.current.applicationContext
    var alarms by remember { mutableStateOf<List<AlarmData>>(emptyList()) }
    var debugText by remember { mutableStateOf("") }
    val currentAlarms by rememberUpdatedState(alarms)

    // Démarre l'écoute quand l'écran apparaît, la coupe quand il disparaît
    DisposableEffect(uid) {
        val listener = uid?.let { id ->
            AlarmRepository.observeUserAlarms(id) { result ->
                HealthDebug.debug("observeUserAlarms -> ${result.size} alarmes")
                alarms = result
            }
        }
        onDispose { listener?.remove() }
    }

    // Debug + progression en continu tant que l'écran est visible
    // todo: voir en background pour envoyer notif
    LaunchedEffect(uid) {
        if (uid == null) return@LaunchedEffect
        while (true) {
            if (HealthDebug.ENABLED) debugText = StepsReader.debugSnapshot(appContext)

            currentAlarms
                .filter { it.results[uid]?.finished == false }
                .forEach { refreshProgress(appContext, it, uid) }

            delay(HealthDebug.POLL_MS)
        }
    }

    AlarmsContent(
        alarms = alarms.sortedWith(compareBy({ it.hour }, { it.minute })),
        onAlarmClick = onAlarmClick,
        modifier = modifier
    )
}

@Composable
private fun AlarmsContent(alarms: List<AlarmData>, modifier: Modifier = Modifier, onAlarmClick: (String) -> Unit = {}) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        // Text tes réveils
        Text(
            text = stringResource(R.string.your_alarms),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(20.dp))


        if (alarms.isEmpty()) {

            // Pas de réveils
            Text(
                text = stringResource(R.string.no_alarms),
                color = GraySubtitle,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                alarms.forEach { alarm ->
                    AlarmCard(
                        alarmData = alarm,
                        onClick = { onAlarmClick(alarm.groupId) },
                        modifier = if (alarm.enabled) {
                            Modifier.border(1.dp, GreenAccent, MediumRoundedCornerShape)
                        } else Modifier
                    )
                }
            }
        }
    }
}

@Composable
private fun AlarmCard(alarmData: AlarmData,
                      onClick: () -> Unit,
                      modifier: Modifier = Modifier) {

    Column(modifier = modifier.fillMaxWidth()
        .background(FieldBg, MediumRoundedCornerShape)
        .clickable(enabled = alarmData.groupId.isNotBlank()) { onClick() }
        .padding(14.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            // Alarm time
            Text(
                text = stringResource(R.string.alarm_time, alarmData.hour, alarmData.minute),
                color = Color.White,
                style = MaterialTheme.typography.displayMedium
            )

            // Status à droite
            Spacer(modifier = Modifier.weight(1f))

            // Alarm status badge
            Box(modifier = Modifier.background(if (alarmData.success) GreenAccent.copy(alpha = 0.15f) else RedFail.copy(alpha = 0.15f), LittleRoundedCornerShape).padding(horizontal = 10.dp, vertical = 6.dp)) {
                Text(
                    text = when (alarmData.state) {
                        AlarmState.NONE -> stringResource(R.string.alarm_none)
                        AlarmState.IN_PROGRESS -> stringResource(R.string.alarm_in_progress)
                        AlarmState.DONE -> if (alarmData.success) stringResource(R.string.alarm_success) else stringResource(R.string.alarm_fail)
                    },
                    color = if (alarmData.success) GreenAccent else RedFail,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Text Objectif réveil
        Text(
            text = objectiveText(LocalContext.current, alarmData.stepGoal, alarmData.durationMinutes),
            color = GraySubtitle,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(14.dp))

        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

        Spacer(modifier = Modifier.height(14.dp))

        // Membres
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "\uD83D\uDE0F\uD83D\uDE0F\uD83D\uDE0F", fontSize = 14.sp) // todo : (emojis personnes)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlarmsScreenPreview() {
    HealthyBetTheme {
        AlarmsContent(
            alarms = listOf(
                AlarmData(
                    id = "1", hour = 6, minute = 30,
                    stepGoal = 5000, durationMinutes = 45,
                    advancement = AlarmState.IN_PROGRESS.name,
                    enabled = true
                ),
                AlarmData(
                    id = "2", hour = 7, minute = 0,
                    stepGoal = 8000, durationMinutes = 90,
                    advancement = AlarmState.DONE.name,
                    success = true,
                    enabled = false
                )
            )
        )
    }
}