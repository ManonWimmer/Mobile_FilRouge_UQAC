package com.grman.healthybet.screens.groups

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
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
import com.grman.healthybet.alarm.AlarmSync
import com.grman.healthybet.data.alarms.AlarmData
import com.grman.healthybet.data.alarms.AlarmRepository
import com.grman.healthybet.data.alarms.GetObjectiveText
import com.grman.healthybet.data.groups.GroupMember
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


@Composable
fun GroupDetailScreen(
    groupId: String,
    viewModel: GroupsViewModel,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCreateAlarmClick: (String) -> Unit = {}) {

    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    val group = viewModel.myGroups.find { it.id == groupId } // Get groupe avec id de la liste
    val isMember = viewModel.myGroups.any { it.id == groupId }
    var showLeaveDialog by remember { mutableStateOf(false) }

    // Alarmes du groupe en temps réel
    var groupAlarms by remember { mutableStateOf<List<AlarmData>>(emptyList()) }
    DisposableEffect(groupId) {
        val listener = AlarmRepository.observeGroupAlarms(groupId) { groupAlarms = it }
        onDispose { listener.remove() }
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

                GroupAlarmCard(
                    alarm = alarm,
                    ownerName = owner?.username.orEmpty(),
                    ownerEmoji = owner?.emoji ?: "🤔",
                    participantEmojis = participants.joinToString("") { it.emoji },
                    participantCount = alarm.participantIds.size,
                    joined = joined,
                    onToggleJoin = {
                        if (uid != null) {
                            if (joined) AlarmRepository.leave(alarm.id, uid)
                            else AlarmRepository.join(alarm.id, uid)
                            AlarmSync.sync(context, uid)
                        }
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
}

@Composable
private fun GroupAlarmCard(
    alarm: AlarmData,
    ownerName: String,
    ownerEmoji: String,
    participantEmojis: String,
    participantCount: Int,
    joined: Boolean,
    onToggleJoin: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().background(FieldBg, HighRoundedCornerShape).padding(16.dp)) {

        // Créateur + heure
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(36.dp).background(Color.White.copy(alpha = 0.08f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = ownerEmoji, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = ownerName,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "%02d:%02d".format(alarm.hour, alarm.minute),
                color = GreenAccent,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Objectif
        Text(text = GetObjectiveText(alarm.stepGoal, alarm.durationMinutes), color = Color.White, style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(10.dp))

        // Inscrits
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = participantEmojis, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.registered_count, participantCount),
                color = GraySubtitle,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onToggleJoin,
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = LittleRoundedCornerShape,
            colors = ButtonDefaults.buttonColors(containerColor = if (joined) RedFail else GreenAccent)
        ) {
            Text(
                text = stringResource(if (joined) R.string.leave_alarm else R.string.join_alarm),
                style = MaterialTheme.typography.labelSmall
            )
        }
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

@Preview(showBackground = true)
@Composable
fun GroupDetailScreenPreview() {
    HealthyBetTheme {
        GroupDetailScreen(groupId = "", viewModel = GroupsViewModel())
    }
}