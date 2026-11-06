package com.grman.healthybet.screens.groups

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.grman.healthybet.R
import com.grman.healthybet.alarm.AlarmScheduler
import com.grman.healthybet.alarm.AlarmSync
import com.grman.healthybet.data.alarms.AlarmData
import com.grman.healthybet.data.alarms.AlarmRepository
import com.grman.healthybet.data.alarms.objectiveText
import com.grman.healthybet.data.groups.GroupMember
import com.grman.healthybet.data.groups.GroupRepository
import com.grman.healthybet.data.groups.GroupsOverview
import com.grman.healthybet.items.BackButton
import com.grman.healthybet.ui.theme.FieldBg
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.HealthyBetTheme
import com.grman.healthybet.ui.theme.HighRoundedCornerShape
import com.grman.healthybet.ui.theme.LittleRoundedCornerShape
import com.grman.healthybet.ui.theme.MediumRoundedCornerShape
import com.grman.healthybet.ui.theme.RedFail
import com.grman.healthybet.viewmodels.GroupsViewModel
import androidx.compose.ui.platform.LocalInspectionMode
import com.grman.healthybet.data.alarms.hasRung
import com.grman.healthybet.items.AlarmCard
import com.grman.healthybet.items.AlarmGroup
import com.grman.healthybet.items.AlarmOwner
import com.grman.healthybet.items.DeleteAlarmDialog
import com.grman.healthybet.items.AlarmActions


@Composable
fun GroupDetailScreen(
    groupId: String,
    viewModel: GroupsViewModel,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCreateAlarmClick: (String) -> Unit = {}) {

    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val uid = if (isPreview) null else FirebaseAuth.getInstance().currentUser?.uid

    val group = viewModel.myGroups.find { it.id == groupId } // Get groupe avec id de la liste
    val isMember = viewModel.myGroups.any { it.id == groupId }

    var showLeaveDialog by remember { mutableStateOf(false) }
    var alarmToDelete by remember { mutableStateOf<AlarmData?>(null) }

    // Alarmes du groupe en temps réel
    var groupAlarms by remember { mutableStateOf<List<AlarmData>>(emptyList()) }
    DisposableEffect(groupId) {
        if (isPreview) return@DisposableEffect onDispose {}
        val listener = AlarmRepository.observeGroupAlarms(groupId) { groupAlarms = it }
        onDispose { listener.remove() }
    }

    // Update de classement à chaque réouverture + quand alarme change
    LaunchedEffect(groupId, groupAlarms) {
        viewModel.loadGroupMembers(groupId)
    }

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 16.dp)) {

        // Header
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

            // Bouton retour
            BackButton(
                onClick = onBackClick,
            )

            // Nom du groupe + nombre de membres
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
                    // Bouton quitter le groupe
                    HeaderButton(
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        description = stringResource(R.string.leave_group),
                        onClick = { showLeaveDialog = true }
                    )
                } else {
                    // Bouton rejoindre le groupe
                    Button(
                        onClick = { viewModel.joinGroup(groupId) },
                        modifier = Modifier.height(40.dp),
                        shape = LittleRoundedCornerShape,
                        colors = ButtonDefaults.buttonColors(containerColor = GreenAccent)
                    ) {
                        Text(text = stringResource(R.string.join_group), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Afficher alarms si membre du groupe
        if (isMember) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.group_alarms),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { onCreateAlarmClick(groupId) },
                    shape = LittleRoundedCornerShape,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenAccent)
                ) {
                    Text(stringResource(R.string.create_alarm), style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (groupAlarms.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_group_alarm),
                    color = GraySubtitle,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            groupAlarms.sortedWith(compareBy({ it.hour }, { it.minute })).forEach { alarm ->
                val joined = uid != null && uid in alarm.participantIds
                val owner = viewModel.groupMembers.find { it.id == alarm.ownerId }
                val participants = viewModel.groupMembers.filter { it.id in alarm.participantIds }

                AlarmCard(
                    alarm = alarm,
                    result = uid?.let { alarm.results[it] },
                    owner = AlarmOwner(owner?.emoji ?: "🤔", owner?.username.orEmpty()),
                    group = group?.let { AlarmGroup(it.emoji, it.name) },
                    participantEmojis = participants.joinToString("") { it.emoji },
                    participantCount = alarm.participantIds.size,
                    actions = {
                        AlarmActions(
                            joined = joined,
                            isOwner = uid != null && alarm.ownerId == uid,
                            canLeave = uid?.let { alarm.results[it] } == null,
                            canDelete = !alarm.hasRung,
                            onToggleJoin = {
                                if (uid != null) {
                                    if (joined) AlarmRepository.leave(alarm.id, uid)
                                    else AlarmRepository.join(alarm.id, uid)
                                    AlarmSync.sync(context, uid)
                                }
                            },
                            onDelete = { alarmToDelete = alarm }
                        )
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        LaunchedEffect(groupId) { viewModel.loadGroupMembers(groupId) } // Get group members

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = stringResource(R.string.leaderboard), color = Color.White, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))

        viewModel.groupMembers.forEachIndexed { index, member ->
            MemberRow(rank = index + 1, member = member)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    // Confirmation avant de quitter
    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false },
            title = { Text(stringResource(R.string.leave_group_question)) },
            text = { Text(stringResource(R.string.leave_group_description)) },
            confirmButton = {
                TextButton(onClick = {
                    showLeaveDialog = false
                    viewModel.leaveGroup(groupId, onSuccess = onBackClick)
                }) {
                    Text(stringResource(R.string.quit), color = RedFail)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    alarmToDelete?.let { alarm ->
        DeleteAlarmDialog(alarm = alarm, onDismiss = { alarmToDelete = null })
    }
}

// Bouton carré du header (retour, paramètres)
@Composable
private fun HeaderButton(icon: ImageVector, description: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(40.dp).background(FieldBg, MediumRoundedCornerShape).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = description, tint = Color.White, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun MemberRow(rank: Int, member: GroupMember) {
    Row(
        modifier = Modifier.fillMaxWidth().background(FieldBg, MediumRoundedCornerShape).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "$rank", color = GraySubtitle, modifier = Modifier.width(24.dp))
        Text(text = member.emoji, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = member.username, color = Color.White, modifier = Modifier.weight(1f))
        Text(text = "${member.points} pts", color = GreenAccent)
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