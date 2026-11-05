package com.grman.healthybet.screens.alarms

import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import com.google.firebase.auth.FirebaseAuth
import com.grman.healthybet.R
import com.grman.healthybet.data.alarms.AlarmData
import com.grman.healthybet.data.alarms.AlarmRepository
import com.grman.healthybet.data.alarms.AlarmResult
import com.grman.healthybet.data.alarms.AlarmState
import com.grman.healthybet.data.alarms.objectiveText
import com.grman.healthybet.data.health.HealthDebug
import com.grman.healthybet.data.health.StepsReader
import com.grman.healthybet.data.health.refreshProgress
import com.grman.healthybet.items.AlarmCard
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

    // Health permission
    val healthPermissionLauncher = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { }

    LaunchedEffect(Unit) {
        if (StepsReader.isAvailable(appContext)) {
            val granted = HealthConnectClient.getOrCreate(appContext)
                .permissionController.getGrantedPermissions()
            if (!granted.containsAll(StepsReader.permissions)) {
                healthPermissionLauncher.launch(StepsReader.permissions)
            }
        }
    }

    // Mise à jour des pas toutes les POLL_MS tant que l'écran est visible
    // todo: voir en background pour envoyer notif
    LaunchedEffect(uid) {
        if (uid == null) return@LaunchedEffect
        while (true) {
            if (HealthDebug.ENABLED) debugText = StepsReader.debugSnapshot(appContext)

            // currentAlarms est toujours la dernière liste reçue de Firestore
            currentAlarms
                .filter { it.results[uid]?.finished == false } // seulement les alarmes en cours
                .forEach { refreshProgress(appContext, it, uid) }

            delay(HealthDebug.POLL_MS)
        }
    }

    AlarmsContent(
        alarms = alarms.sortedWith(compareBy({ it.hour }, { it.minute })),
        uid = uid,
        onAlarmClick = onAlarmClick,
        modifier = modifier
    )
}

@Composable
private fun AlarmsContent(
    alarms: List<AlarmData>,
    uid: String?,
    modifier: Modifier = Modifier,
    onAlarmClick: (String) -> Unit = {}) {

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
                        alarm = alarm,
                        result = uid?.let { alarm.results[it] },
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

@Preview(showBackground = true)
@Composable
fun AlarmsScreenPreview() {
    HealthyBetTheme {
        AlarmsContent(
            uid = "preview",
            alarms = listOf(
                AlarmData(
                    id = "1", hour = 6, minute = 30,
                    stepGoal = 5000, durationMinutes = 45,
                    results = mapOf("me" to AlarmResult(startedAt = 0L, steps = 2300))
                ),
                AlarmData(
                    id = "2", hour = 7, minute = 0,
                    stepGoal = 8000, durationMinutes = 90,
                    results = mapOf("me" to AlarmResult(startedAt = 0L, steps = 8000, success = true, finished = true))
                )
            )
        )
    }
}