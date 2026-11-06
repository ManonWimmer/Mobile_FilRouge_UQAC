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
import com.grman.healthybet.alarm.AlarmSync
import com.grman.healthybet.data.alarms.AlarmData
import com.grman.healthybet.data.alarms.AlarmRepository
import com.grman.healthybet.data.alarms.AlarmResult
import com.grman.healthybet.data.alarms.AlarmState
import com.grman.healthybet.data.alarms.hasRung
import com.grman.healthybet.data.alarms.objectiveText
import com.grman.healthybet.data.groups.Group
import com.grman.healthybet.data.groups.GroupMember
import com.grman.healthybet.data.health.HealthDebug
import com.grman.healthybet.data.health.StepsReader
import com.grman.healthybet.data.health.refreshProgress
import com.grman.healthybet.items.AlarmActions
import com.grman.healthybet.items.AlarmCard
import com.grman.healthybet.items.AlarmGroup
import com.grman.healthybet.items.DeleteAlarmDialog
import com.grman.healthybet.ui.theme.FieldBg
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.HealthyBetTheme
import com.grman.healthybet.ui.theme.LittleRoundedCornerShape
import com.grman.healthybet.ui.theme.MediumRoundedCornerShape
import com.grman.healthybet.ui.theme.RedFail
import com.grman.healthybet.viewmodels.GroupsViewModel
import kotlinx.coroutines.delay
import kotlin.comparisons.compareBy


@Composable
fun AlarmsScreen(modifier: Modifier = Modifier,
                 groupsViewModel: GroupsViewModel,
                 onAlarmClick: (String) -> Unit = {}) {

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val appContext = LocalContext.current.applicationContext
    var alarms by remember { mutableStateOf<List<AlarmData>>(emptyList()) }
    var debugText by remember { mutableStateOf("") }
    val currentAlarms by rememberUpdatedState(alarms)
    var alarmToDelete by remember { mutableStateOf<AlarmData?>(null) }

    var groupAlarms by remember { mutableStateOf<List<AlarmData>>(emptyList()) }
    val myGroupIds = groupsViewModel.myGroups.map { it.id }

    // Noms des groupes
    LaunchedEffect(Unit) { groupsViewModel.loadGroups() }

    // Membres des groupes des alarmes (pour les emojis des inscrits)
    val groupIds = (alarms + groupAlarms).map { it.groupId }.filter { it.isNotBlank() }.distinct()
    LaunchedEffect(groupIds) { groupsViewModel.loadMembersForGroups(groupIds) }

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

    DisposableEffect(myGroupIds) {
        if (myGroupIds.isEmpty()) {
            groupAlarms = emptyList()
            return@DisposableEffect onDispose {}
        }
        val listener = AlarmRepository.observeAlarmsOfGroups(myGroupIds) { groupAlarms = it }
        onDispose { listener.remove() }
    }

    // alarmes des groupes de l'user ou il est pas inscrit
    val availableAlarms = groupAlarms
        .filter { uid != null && uid !in it.participantIds && it.enabled }
        .sortedWith(compareBy({ it.hour }, { it.minute }))

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
        availableAlarms = availableAlarms,
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

        // Section 1 : alarmes de groupes avec user inscrit
        SectionTitle(stringResource(R.string.your_alarms))

        if (alarms.isEmpty()) {
            EmptyText(stringResource(R.string.no_alarms))
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                alarms.forEach { alarm ->
                    JoinedAlarmItem(
                        alarm = alarm,
                        uid = uid,
                        group = groups.find { it.id == alarm.groupId },
                        members = membersByGroup[alarm.groupId].orEmpty(),
                        onClick = onAlarmClick,
                        onLeave = onLeave,
                        onDelete = onDelete
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Section 2 : alarmes de mes groupes où je peux m'inscrire
        SectionTitle(stringResource(R.string.available_alarms))

        if (availableAlarms.isEmpty()) {
            EmptyText(stringResource(R.string.no_available_alarms))
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                availableAlarms.forEach { alarm ->
                    AvailableAlarmItem(
                        alarm = alarm,
                        uid = uid,
                        group = groups.find { it.id == alarm.groupId },
                        members = membersByGroup[alarm.groupId].orEmpty(),
                        onClick = onAlarmClick,
                        onJoin = onJoin,
                        onDelete = onDelete
                    )
                }
            }
        }
    }
}

// Alarme où je suis inscrit : bordure verte si active, bouton « Se désinscrire »
@Composable
private fun JoinedAlarmItem(
    alarm: AlarmData,
    uid: String?,
    group: Group?,
    members: List<GroupMember>,
    onClick: (String) -> Unit,
    onLeave: (AlarmData) -> Unit,
    onDelete: (AlarmData) -> Unit
) {
    val participants = members.filter { it.id in alarm.participantIds }

    AlarmCard(
        alarm = alarm,
        result = uid?.let { alarm.results[it] },
        group = group?.let { AlarmGroup(it.emoji, it.name) },
        participantEmojis = participants.joinToString("") { it.emoji },
        participantCount = alarm.participantIds.size,
        onClick = if (alarm.groupId.isNotBlank()) { { onClick(alarm.groupId) } } else null,
        actions = {
            AlarmActions(
                joined = true,
                isOwner = uid != null && alarm.ownerId == uid,
                canLeave = uid?.let { alarm.results[it] } == null,
                canDelete = !alarm.hasRung,
                onToggleJoin = { onLeave(alarm) },
                onDelete = { onDelete(alarm) }
            )
        },
        modifier = if (alarm.enabled) {
            Modifier.border(1.dp, GreenAccent, MediumRoundedCornerShape)
        } else Modifier
    )
}

// Alarme de mon groupe où je ne suis pas inscrit : pas de résultat, bouton « Rejoindre »
@Composable
private fun AvailableAlarmItem(
    alarm: AlarmData,
    uid: String?,
    group: Group?,
    members: List<GroupMember>,
    onClick: (String) -> Unit,
    onJoin: (AlarmData) -> Unit,
    onDelete: (AlarmData) -> Unit
) {
    val participants = members.filter { it.id in alarm.participantIds }

    AlarmCard(
        alarm = alarm,
        result = null, // je ne participe pas : pas de progression à afficher
        group = group?.let { AlarmGroup(it.emoji, it.name) },
        participantEmojis = participants.joinToString("") { it.emoji },
        participantCount = alarm.participantIds.size,
        onClick = if (alarm.groupId.isNotBlank()) { { onClick(alarm.groupId) } } else null,
        actions = {
            AlarmActions(
                joined = false,
                isOwner = uid != null && alarm.ownerId == uid,
                canLeave = true,
                canDelete = !alarm.hasRung,
                onToggleJoin = { onJoin(alarm) },
                onDelete = { onDelete(alarm) }
            )
        }
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, color = Color.White, style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
private fun EmptyText(text: String) {
    Text(text = text, color = GraySubtitle, style = MaterialTheme.typography.bodyMedium)
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