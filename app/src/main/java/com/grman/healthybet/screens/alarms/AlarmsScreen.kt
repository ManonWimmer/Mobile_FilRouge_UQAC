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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.grman.healthybet.data.alarms.AlarmData
import com.grman.healthybet.data.alarms.AlarmRepository
import com.grman.healthybet.data.alarms.AlarmState
import com.grman.healthybet.ui.theme.FieldBg
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.HealthyBetTheme
import com.grman.healthybet.ui.theme.LittleRoundedCornerShape
import com.grman.healthybet.ui.theme.MediumRoundedCornerShape
import com.grman.healthybet.ui.theme.RedFail
import kotlin.comparisons.compareBy


@Composable
fun AlarmsScreen(modifier: Modifier = Modifier, onAlarmClick: (String) -> Unit = {}) {

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var alarms by remember { mutableStateOf<List<AlarmData>>(emptyList()) }

    // Démarre l'écoute quand l'écran apparaît, la coupe quand il disparaît
    DisposableEffect(uid) {
        val listener = uid?.let { id ->
            AlarmRepository.observeUserAlarms(id) { result -> alarms = result }
        }
        onDispose { listener?.remove() }
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
        Text(
            text = "Tes Réveils",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (alarms.isEmpty()) {
            Text(
                text = "Aucun réveil pour l'instant",
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
                text = "%02d:%02d".format(alarmData.hour, alarmData.minute),
                color = Color.White,
                style = MaterialTheme.typography.displayMedium
            )

            // Status à droite
            Spacer(modifier = Modifier.weight(1f))

            // Alarm status badge
            Box(modifier = Modifier.background(if (alarmData.success) GreenAccent.copy(alpha = 0.15f) else RedFail.copy(alpha = 0.15f), LittleRoundedCornerShape).padding(horizontal = 10.dp, vertical = 6.dp)) {
                Text(
                    text = when (alarmData.state) {
                        AlarmState.NONE -> "Programmé"
                        AlarmState.EN_COURS -> "En cours"
                        AlarmState.FINIT -> if (alarmData.success) "Réussi ✅" else "Raté ❌"
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
            text = alarmData.objective,
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
                    objective = "🏃 Courir 5km avant 07:15",
                    advancement = AlarmState.EN_COURS.name
                ),
                AlarmData(
                    id = "2", hour = 7, minute = 0,
                    objective = "👟 8000 pas",
                    advancement = AlarmState.FINIT.name,
                    success = true
                )
            )
        )
    }
}