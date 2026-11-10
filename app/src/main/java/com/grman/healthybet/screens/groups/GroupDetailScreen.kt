package com.grman.healthybet.screens.groups

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.grman.healthybet.R
import com.grman.healthybet.alarm.AlarmSync
import com.grman.healthybet.data.alarms.AlarmData
import com.grman.healthybet.data.alarms.AlarmRepository
import com.grman.healthybet.data.groups.Group
import com.grman.healthybet.data.groups.GroupMember
import com.grman.healthybet.data.groups.GroupRepository
import com.grman.healthybet.data.groups.GroupsOverview
import com.grman.healthybet.items.alarms.AlarmListItem
import com.grman.healthybet.items.common.AppButton
import com.grman.healthybet.items.common.BackButton
import com.grman.healthybet.items.common.ConfirmDialog
import com.grman.healthybet.items.alarms.DeleteAlarmDialog
import com.grman.healthybet.items.common.EmptyText
import com.grman.healthybet.items.groups.MemberRow
import com.grman.healthybet.items.common.SectionTitle
import com.grman.healthybet.items.common.SquareIconButton
import com.grman.healthybet.items.alarms.sortedByTime
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.HealthyBetTheme
import com.grman.healthybet.viewmodels.GroupsViewModel

@Composable
fun GroupDetailScreen(
    groupId: String,
    viewModel: GroupsViewModel,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCreateAlarmClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val uid = if (isPreview) null else FirebaseAuth.getInstance().currentUser?.uid

    val group = viewModel.myGroups.find { it.id == groupId }
    val isMember = group != null

    var showLeaveDialog by remember { mutableStateOf(false) }
    var alarmToDelete by remember { mutableStateOf<AlarmData?>(null) }

    // Alarmes du groupe en temps réel
    var groupAlarms by remember { mutableStateOf<List<AlarmData>>(emptyList()) }
    DisposableEffect(groupId) {
        if (isPreview) return@DisposableEffect onDispose {}
        val listener = AlarmRepository.observeGroupAlarms(groupId) { groupAlarms = it }
        onDispose { listener.remove() }
    }

    // Mise à jour du classement à chaque réouverture + quand une alarme change
    LaunchedEffect(groupId, groupAlarms) { viewModel.loadGroupMembers(groupId) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        GroupHeader(
            group = group,
            isMember = isMember,
            onBackClick = onBackClick,
            onLeaveClick = { showLeaveDialog = true },
            onJoinClick = { viewModel.joinGroup(groupId) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Alarmes : seulement si membre du groupe
        if (isMember) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.group_alarms),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                AppButton(
                    text = stringResource(R.string.create_alarm),
                    onClick = { onCreateAlarmClick(groupId) },
                    fillWidth = false,
                    height = 40.dp,
                    textStyle = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (groupAlarms.isEmpty()) {
                EmptyText(stringResource(R.string.no_group_alarm))
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                groupAlarms.sortedByTime().forEach { alarm ->
                    AlarmListItem(
                        alarm = alarm,
                        uid = uid,
                        joined = uid != null && uid in alarm.participantIds,
                        group = group,
                        members = viewModel.groupMembers,
                        showOwner = true,
                        onToggleJoin = { a ->
                            if (uid != null) {
                                if (uid in a.participantIds) AlarmRepository.leave(a.id, uid)
                                else AlarmRepository.join(a.id, uid)
                                AlarmSync.sync(context, uid)
                            }
                        },
                        onDelete = { alarmToDelete = it }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Classement
        SectionTitle(stringResource(R.string.leaderboard), spacing = 12.dp)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            viewModel.groupMembers.forEachIndexed { index, member ->
                MemberRow(rank = index + 1, member = member)
            }
        }
    }

    // Confirmation avant de quitter
    if (showLeaveDialog) {
        ConfirmDialog(
            title = stringResource(R.string.leave_group_question),
            text = stringResource(R.string.leave_group_description),
            confirmText = stringResource(R.string.quit),
            onConfirm = {
                showLeaveDialog = false
                viewModel.leaveGroup(groupId, onSuccess = onBackClick)
            },
            onDismiss = { showLeaveDialog = false }
        )
    }

    alarmToDelete?.let { alarm ->
        DeleteAlarmDialog(alarm = alarm, onDismiss = { alarmToDelete = null })
    }
}

// Retour + nom du groupe + nombre de membres + bouton quitter / rejoindre
@Composable
private fun GroupHeader(
    group: Group?,
    isMember: Boolean,
    onBackClick: () -> Unit,
    onLeaveClick: () -> Unit,
    onJoinClick: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        BackButton(onClick = onBackClick)

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${group?.name ?: stringResource(R.string.default_group_name)} ${group?.emoji ?: ""}",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.active_members, group?.memberCount ?: 0),
                color = GraySubtitle,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(15.dp))

            if (isMember) {
                SquareIconButton(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    description = stringResource(R.string.leave_group),
                    onClick = onLeaveClick
                )
            } else {
                AppButton(
                    text = stringResource(R.string.join_group),
                    onClick = onJoinClick,
                    fillWidth = false,
                    height = 40.dp,
                    textStyle = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
fun GroupDetailScreenPreview() {
    val fakeRepo = object : GroupRepository {
        override suspend fun getGroups(): GroupsOverview {
            TODO("Retourner un GroupsOverview de test")
        }
        override suspend fun createGroup(name: String, emoji: String, description: String) {}
        override suspend fun joinGroup(groupId: String) {}
        override suspend fun leaveGroup(groupId: String) {}
        override suspend fun getGroupMembers(groupId: String): List<GroupMember> = emptyList()
    }

    HealthyBetTheme {
        GroupDetailScreen(groupId = "", viewModel = GroupsViewModel(fakeRepo))
    }
}