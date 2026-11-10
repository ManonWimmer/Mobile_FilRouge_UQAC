package com.grman.healthybet.items.alarms

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.grman.healthybet.R
import com.grman.healthybet.alarm.AlarmScheduler
import com.grman.healthybet.data.alarms.AlarmData
import com.grman.healthybet.data.alarms.AlarmRepository
import com.grman.healthybet.data.alarms.hasRung
import com.grman.healthybet.data.groups.Group
import com.grman.healthybet.data.groups.GroupMember
import com.grman.healthybet.items.common.AppOutlinedButton
import com.grman.healthybet.items.common.ConfirmDialog
import com.grman.healthybet.items.common.EmptyText
import com.grman.healthybet.items.common.SectionTitle
import com.grman.healthybet.items.common.AppButton
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.MediumRoundedCornerShape
import com.grman.healthybet.ui.theme.RedFail

// Section : titre + (texte vide | liste d'alarmes)
@Composable
fun AlarmSection(
    title: String,
    emptyText: String,
    alarms: List<AlarmData>,
    itemContent: @Composable (AlarmData) -> Unit
) {
    SectionTitle(title)
    if (alarms.isEmpty()) {
        EmptyText(emptyText)
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            alarms.forEach { itemContent(it) }
        }
    }
}

// Trie les alarmes par heure puis minute
fun List<AlarmData>.sortedByTime() = sortedWith(compareBy({ it.hour }, { it.minute }))

// Une alarme de la liste.
// joined = true  : je suis inscrit (bordure verte si active, bouton « Se désinscrire »)
// joined = false : alarme de mon groupe où je ne suis pas inscrit (bouton « Rejoindre »)
// onClick = null : la carte n'est pas cliquable
// showOwner      : affiche le créateur (trouvé dans members)
@Composable
fun AlarmListItem(
    alarm: AlarmData,
    uid: String?,
    joined: Boolean,
    group: Group?,
    members: List<GroupMember>,
    onToggleJoin: (AlarmData) -> Unit,
    onDelete: (AlarmData) -> Unit,
    onClick: ((String) -> Unit)? = null,
    showOwner: Boolean = false
) {
    // Si je ne participe pas : pas de progression à afficher
    val myResult = if (joined && uid != null) alarm.results[uid] else null
    val participantEmojis = members.filter { it.id in alarm.participantIds }.joinToString("") { it.emoji }
    val owner = if (showOwner) {
        val member = members.find { it.id == alarm.ownerId }
        AlarmOwner(member?.emoji ?: "🤔", member?.username.orEmpty())
    } else null

    AlarmCard(
        alarm = alarm,
        result = myResult,
        owner = owner,
        group = group?.let { AlarmGroup(it.emoji, it.name) },
        participantEmojis = participantEmojis,
        participantCount = alarm.participantIds.size,
        onClick = if (onClick != null && alarm.groupId.isNotBlank()) { { onClick(alarm.groupId) } } else null,
        actions = {
            AlarmActions(
                joined = joined,
                isOwner = uid != null && alarm.ownerId == uid,
                canLeave = myResult == null,
                canDelete = !alarm.hasRung,
                onToggleJoin = { onToggleJoin(alarm) },
                onDelete = { onDelete(alarm) }
            )
        },
        modifier = if (joined && alarm.enabled) {
            Modifier.border(1.dp, GreenAccent, MediumRoundedCornerShape)
        } else Modifier
    )
}

// Confirmation de suppression
@Composable
fun DeleteAlarmDialog(alarm: AlarmData, onDismiss: () -> Unit) {
    val context = LocalContext.current
    ConfirmDialog(
        title = stringResource(R.string.delete_alarm_question),
        text = stringResource(R.string.delete_alarm_description),
        confirmText = stringResource(R.string.delete),
        onConfirm = {
            AlarmRepository.delete(alarm.id)
            AlarmScheduler.cancel(context, alarm.id)
            onDismiss()
        },
        onDismiss = onDismiss
    )
}

// Rejoindre / quitter + supprimer (créateur seulement)
@Composable
fun AlarmActions(
    joined: Boolean,
    isOwner: Boolean,
    canLeave: Boolean,    // faux si mon alarme a déjà sonné
    canDelete: Boolean,   // faux si l'alarme a déjà sonné
    onToggleJoin: () -> Unit,
    onDelete: () -> Unit
) {
    Column {
        // rejoindre ou quitter (seulement avant la sonnerie)
        if (!joined || canLeave) {
            AppButton(
                text = stringResource(if (joined) R.string.leave_alarm else R.string.join_alarm),
                onClick = onToggleJoin,
                height = 44.dp,
                containerColor = if (joined) RedFail else GreenAccent,
                textStyle = MaterialTheme.typography.labelSmall
            )
        } else {
            Text(
                text = stringResource(R.string.alarm_locked),
                color = GraySubtitle,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (isOwner && canDelete) {
            Spacer(modifier = Modifier.height(8.dp))
            AppOutlinedButton(
                text = stringResource(R.string.delete_alarm),
                onClick = onDelete,
                color = RedFail,
                height = 44.dp,
                textStyle = MaterialTheme.typography.labelSmall
            )
        }
    }
}