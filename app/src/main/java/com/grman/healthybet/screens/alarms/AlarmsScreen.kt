package com.grman.healthybet.screens.alarms

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import com.google.firebase.auth.FirebaseAuth
import com.grman.healthybet.R
import com.grman.healthybet.alarm.AlarmSync
import com.grman.healthybet.data.alarms.AlarmData
import com.grman.healthybet.data.alarms.AlarmRepository
import com.grman.healthybet.data.alarms.AlarmResult
import com.grman.healthybet.data.groups.Group
import com.grman.healthybet.data.groups.GroupMember
import com.grman.healthybet.data.health.HealthDebug
import com.grman.healthybet.data.health.StepsReader
import com.grman.healthybet.data.health.refreshProgress
import com.grman.healthybet.items.alarms.AlarmListItem
import com.grman.healthybet.items.alarms.AlarmSection
import com.grman.healthybet.items.alarms.DeleteAlarmDialog
import com.grman.healthybet.ui.theme.HealthyBetTheme
import com.grman.healthybet.viewmodels.GroupsViewModel
import kotlinx.coroutines.delay

private fun List<AlarmData>.sortedByTime() = sortedWith(compareBy({ it.hour }, { it.minute }))

@Composable
fun AlarmsScreen(
    modifier: Modifier = Modifier,
    groupsViewModel: GroupsViewModel,
    onAlarmClick: (String) -> Unit = {}
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val appContext = LocalContext.current.applicationContext

    var alarms by remember { mutableStateOf<List<AlarmData>>(emptyList()) }
    var groupAlarms by remember { mutableStateOf<List<AlarmData>>(emptyList()) }
    var debugText by remember { mutableStateOf("") }
    var alarmToDelete by remember { mutableStateOf<AlarmData?>(null) }
    val currentAlarms by rememberUpdatedState(alarms)
    val myGroupIds = groupsViewModel.myGroups.map { it.id }

    // Noms des groupes
    LaunchedEffect(Unit) { groupsViewModel.loadGroups() }

    // Membres des groupes des alarmes (pour les emojis des inscrits)
    val groupIds = (alarms + groupAlarms).map { it.groupId }.filter { it.isNotBlank() }.distinct()
    LaunchedEffect(groupIds) { groupsViewModel.loadMembersForGroups(groupIds) }

    // Mes alarmes : écoute tant que l'écran est visible
    DisposableEffect(uid) {
        val listener = uid?.let { id ->
            AlarmRepository.observeUserAlarms(id) { result ->
                HealthDebug.debug("observeUserAlarms -> ${result.size} alarmes")
                alarms = result
            }
        }
        onDispose { listener?.remove() }
    }

    // Alarmes de mes groupes
    DisposableEffect(myGroupIds) {
        if (myGroupIds.isEmpty()) {
            groupAlarms = emptyList()
            return@DisposableEffect onDispose {}
        }
        val listener = AlarmRepository.observeAlarmsOfGroups(myGroupIds) { groupAlarms = it }
        onDispose { listener.remove() }
    }

    // Alarmes des groupes de l'user où il n'est pas inscrit
    val availableAlarms = groupAlarms.filter { uid != null && uid !in it.participantIds && it.enabled }

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

            // seulement les alarmes en cours
            currentAlarms
                .filter { it.results[uid]?.finished == false }
                .forEach { refreshProgress(appContext, it, uid) }

            delay(HealthDebug.POLL_MS)
        }
    }

    AlarmsContent(
        alarms = alarms.sortedByTime(),
        uid = uid,
        availableAlarms = availableAlarms.sortedByTime(),
        groups = groupsViewModel.myGroups,
        membersByGroup = groupsViewModel.membersByGroup,
        onAlarmClick = onAlarmClick,
        onLeave = { alarm ->
            if (uid != null) {
                AlarmRepository.leave(alarm.id, uid)
                AlarmSync.sync(appContext, uid)
            }
        },
        onJoin = { alarm ->
            if (uid != null) {
                AlarmRepository.join(alarm.id, uid)
                AlarmSync.sync(appContext, uid)
            }
        },
        onDelete = { alarm -> alarmToDelete = alarm },
        modifier = modifier
    )

    alarmToDelete?.let { alarm ->
        DeleteAlarmDialog(alarm = alarm, onDismiss = { alarmToDelete = null })
    }
}

@Composable
private fun AlarmsContent(
    alarms: List<AlarmData>,
    uid: String?,
    modifier: Modifier = Modifier,
    availableAlarms: List<AlarmData> = emptyList(),
    groups: List<Group> = emptyList(),
    membersByGroup: Map<String, List<GroupMember>> = emptyMap(),
    onAlarmClick: (String) -> Unit = {},
    onLeave: (AlarmData) -> Unit = {},
    onJoin: (AlarmData) -> Unit = {},
    onDelete: (AlarmData) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Section 1 : alarmes où je suis inscrit
        AlarmSection(
            title = stringResource(R.string.your_alarms),
            emptyText = stringResource(R.string.no_alarms),
            alarms = alarms
        ) { alarm ->
            AlarmListItem(
                alarm = alarm,
                uid = uid,
                joined = true,
                group = groups.find { it.id == alarm.groupId },
                members = membersByGroup[alarm.groupId].orEmpty(),
                onClick = onAlarmClick,
                onToggleJoin = onLeave,
                onDelete = onDelete
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Section 2 : alarmes de mes groupes où je peux m'inscrire
        AlarmSection(
            title = stringResource(R.string.available_alarms),
            emptyText = stringResource(R.string.no_available_alarms),
            alarms = availableAlarms
        ) { alarm ->
            AlarmListItem(
                alarm = alarm,
                uid = uid,
                joined = false,
                group = groups.find { it.id == alarm.groupId },
                members = membersByGroup[alarm.groupId].orEmpty(),
                onClick = onAlarmClick,
                onToggleJoin = onJoin,
                onDelete = onDelete
            )
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