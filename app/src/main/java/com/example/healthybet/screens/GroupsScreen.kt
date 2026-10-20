package com.grman.healthybet.screens

import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grman.healthybet.ui.theme.FieldBg
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.HealthyBetTheme
import com.grman.healthybet.ui.theme.LittleRoundedCornerShape
import com.grman.healthybet.ui.theme.MediumRoundedCornerShape

@Composable
fun GroupsScreen(
    modifier: Modifier = Modifier,
    viewModel: GroupsViewModel,
    onCreateGroupClick: () -> Unit = {},
    onGroupClick: (String) -> Unit = {}
) {

    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {

        // Header
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Groupes",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            // Bouton +
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(GreenAccent.copy(alpha = 0.15f), MediumRoundedCornerShape)
                    .clickable { onCreateGroupClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Créer un groupe",
                    tint = GreenAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        GroupTabs(selectedTab = selectedTab, onTabSelected = { selectedTab = it })

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            // My groups
            0 -> MyGroupsContent(
                groups = viewModel.myGroups,
                onGroupClick = onGroupClick,
                onCreateGroupClick = onCreateGroupClick,
                modifier = Modifier.weight(1f)
            )
            // todo : else Search groups
        }
    }
}

// Switch Mes groupes / Découvrir
@Composable
private fun GroupTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("Mes groupes", "Découvrir")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FieldBg, MediumRoundedCornerShape)
            .border(1.dp, Color.White.copy(alpha = 0.1f), MediumRoundedCornerShape)
            .padding(4.dp)
    ) {
        tabs.forEachIndexed { index, label ->
            val selected = index == selectedTab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (selected) GreenAccent.copy(alpha = 0.15f) else Color.Transparent,
                        LittleRoundedCornerShape
                    )
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (selected) GreenAccent else GraySubtitle,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun MyGroupsContent(groups: List<Group>, onGroupClick: (String) -> Unit, onCreateGroupClick: () -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {

        // Pas de groupe
        if (groups.isEmpty()) {
            item {
                Text(
                    text = "Tu n'as pas encore de groupe. Crée le tien ou rejoins-en un dans Découvrir !",
                    color = GraySubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
                )
            }
        }

        items(groups, key = { it.id }) { group ->
            GroupCard(group = group, onClick = { onGroupClick(group.id) })
        }

        // Créer nouveau groupe
        item {
            OutlinedButton(
                onClick = onCreateGroupClick,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = LittleRoundedCornerShape,
                border = androidx.compose.foundation.BorderStroke(1.dp, GreenAccent),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenAccent)
            ) {
                Text(text = "+  Créer un nouveau groupe", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun GroupCard(group: Group, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().background(FieldBg, MediumRoundedCornerShape).border(1.dp, Color.White.copy(alpha = 0.08f), MediumRoundedCornerShape).clickable { onClick() }.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {

                // Nom + emoji du groupe
                Text(
                    text = if (group.emoji.isNotBlank()) "${group.name} ${group.emoji}" else group.name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Nombre de membres
                Text(
                    text = "${group.memberCount} membres",
                    color = GraySubtitle,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Aperçu des membres avec emojis temp (todo: photos de profil)
            Text(text = group.memberEmojis.take(3).joinToString(""), fontSize = 20.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupsScreenPreview() {
    HealthyBetTheme {
        GroupsScreen(viewModel = GroupsViewModel())
    }
}