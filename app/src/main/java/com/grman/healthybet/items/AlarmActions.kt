package com.grman.healthybet.items

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.LittleRoundedCornerShape
import com.grman.healthybet.ui.theme.RedFail

// Rejoindre / quitter + supprimer (créateur seulement)
@Composable
fun ColumnScope.AlarmActions(
    joined: Boolean,
    isOwner: Boolean,
    canLeave: Boolean,    // faux si mon alarme a déjà sonné
    canDelete: Boolean,   // faux si l'alarme a déjà sonné
    onToggleJoin: () -> Unit,
    onDelete: () -> Unit
) {

    // rejoindre ou quitter (seulement avant la sonnerie)
    if (!joined || canLeave) {
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
    } else {
        Text(
            text = stringResource(R.string.alarm_locked),
            color = GraySubtitle,
            style = MaterialTheme.typography.bodySmall
        )
    }

    // Supprimer
    if (isOwner && canDelete) {
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = onDelete,
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = LittleRoundedCornerShape,
            border = BorderStroke(1.dp, RedFail),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = RedFail)
        ) {
            Text(
                text = stringResource(R.string.delete_alarm),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

// Confirmation de suppression
@Composable
fun DeleteAlarmDialog(alarm: AlarmData, onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_alarm_question)) },
        text = { Text(stringResource(R.string.delete_alarm_description)) },
        confirmButton = {
            TextButton(onClick = {
                AlarmRepository.delete(alarm.id)
                AlarmScheduler.cancel(context, alarm.id)
                onDismiss()
            }) { Text(stringResource(R.string.delete), color = RedFail) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}