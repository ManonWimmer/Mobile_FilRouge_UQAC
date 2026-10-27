package com.grman.healthybet.screens.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.grman.healthybet.ui.theme.FieldBg
import com.grman.healthybet.ui.theme.GrayLabel
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.HealthyBetTheme
import com.grman.healthybet.ui.theme.LittleRoundedCornerShape
import com.grman.healthybet.ui.theme.MediumRoundedCornerShape
import com.grman.healthybet.data.groups.Group
import com.grman.healthybet.viewmodels.GroupsViewModel

@Composable
fun SearchGroupContent(viewModel: GroupsViewModel, modifier: Modifier = Modifier) {
    val results = viewModel.filteredDiscover

    Column(modifier = modifier.fillMaxSize()) {

        // Barre de recherche
        GroupTextField(
            value = viewModel.searchQuery,
            onValueChange = { viewModel.searchQuery = it },
            placeholder = "Rechercher un groupe...",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Rechercher",
                    tint = GrayLabel
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (viewModel.searchQuery.isBlank()) "TOUS LES GROUPES" else "RÉSULTATS",
            color = GraySubtitle,
            style = MaterialTheme.typography.labelMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // Pas de groupe
            if (results.isEmpty()) {
                item {
                    Text(
                        text = "Aucun groupe trouvé. Pourquoi ne pas créer le tien ?",
                        color = GraySubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
                    )
                }
            }

            // Chaque groupe : une card
            items(results, key = { it.id }) { group ->
                DiscoverGroupCard(group = group, onJoinClick = { viewModel.joinGroup(group.id) })
            }
        }
    }
}

@Composable
private fun DiscoverGroupCard(group: Group, onJoinClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().background(FieldBg, MediumRoundedCornerShape).border(1.dp, Color.White.copy(alpha = 0.08f), MediumRoundedCornerShape).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {

            // Nom du groupe avec emoji
            Text(
                text = if (group.emoji.isNotBlank()) "${group.name} ${group.emoji}" else group.name,
                color = Color.White,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Nbr de membres
            Text(
                text = "${group.memberCount} membres",
                color = GreenAccent,
                style = MaterialTheme.typography.labelSmall
            )

            // Description
            if (group.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = group.description,
                    color = GraySubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Rejoindre
        Button(
            onClick = onJoinClick,
            shape = LittleRoundedCornerShape,
            colors = ButtonDefaults.buttonColors(containerColor = GreenAccent)
        ) {
            Text(text = "Rejoindre", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchGroupContentPreview() {
    HealthyBetTheme {
        SearchGroupContent(viewModel = GroupsViewModel())
    }
}