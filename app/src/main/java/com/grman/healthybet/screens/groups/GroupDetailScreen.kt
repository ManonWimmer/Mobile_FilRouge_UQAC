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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun GroupDetailScreen(groupId: String, viewModel: GroupsViewModel, modifier: Modifier = Modifier, onBack: () -> Unit = {}) {
    val group = viewModel.myGroups.find { it.id == groupId } // Get groupe avec id de la liste
    var showLeaveDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 16.dp)) {

        // Header
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

            HeaderButton(icon = Icons.AutoMirrored.Filled.ArrowBack, description = "Retour", onClick = onBack)

            // Nom du groupe + nombre de membres
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${group?.name ?: "Groupe"} ${group?.emoji ?: ""}",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${group?.memberCount ?: 0} membres actifs",
                    color = GraySubtitle,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Bouton quitter le groupe
                HeaderButton(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    description = "Quitter le groupe",
                    onClick = { showLeaveDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        GroupAlarmCard()
    }

    // Confirmation avant de quitter
    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false },
            title = { Text("Quitter le groupe ?") },
            text = { Text("Tu ne verras plus ce groupe dans « Mes groupes ». Tu pourras le rejoindre à nouveau depuis Découvrir.") },
            confirmButton = {
                TextButton(onClick = {
                    showLeaveDialog = false
                    viewModel.leaveGroup(groupId, onSuccess = onBack)
                }) {
                    Text("Quitter", color = RedFail)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveDialog = false }) {
                    Text("Annuler")
                }
            }
        )
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

// Alarme d'un membre du groupe (todo: bdd)
@Composable
private fun GroupAlarmCard() {
    Column(modifier = Modifier.fillMaxWidth().background(FieldBg, HighRoundedCornerShape).padding(16.dp)) {

        // Membre emoji + nom + heure (todo: bdd avec celui qui le propose)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).background(Color.White.copy(alpha = 0.08f), CircleShape), contentAlignment = Alignment.Center) {
                Text(text = "🤔", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Hugo M.",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(1f)
            )

            Text(text = "06:15", color = GreenAccent, style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Objectif (todo: bdd)
        Text(
            text = "\uD83E\uDDD7 Escalader 500 marches avant 07h",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        Spacer(modifier = Modifier.height(14.dp))

        // Boutons
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { /* todo: rejoindre l'alarme */ },
                modifier = Modifier.weight(1f).height(44.dp),
                shape = LittleRoundedCornerShape,
                colors = ButtonDefaults.buttonColors(containerColor = GreenAccent)
            ) {
                Text(text = "Rejoindre l'alarme", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupDetailScreenPreview() {
    HealthyBetTheme {
        GroupDetailScreen(groupId = "", viewModel = GroupsViewModel())
    }
}