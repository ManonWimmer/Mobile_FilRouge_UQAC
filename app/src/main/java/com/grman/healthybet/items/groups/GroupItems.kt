package com.grman.healthybet.items.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grman.healthybet.R
import com.grman.healthybet.data.groups.Group
import com.grman.healthybet.data.groups.GroupMember
import com.grman.healthybet.items.common.AppButton
import com.grman.healthybet.items.common.EmptyText
import com.grman.healthybet.ui.theme.FieldBg
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.MediumRoundedCornerShape

// Liste de groupes (vide -> texte centré)
// onJoin != null : bouton « Rejoindre » sur les groupes dont je ne suis pas membre
// footer : élément affiché en bas de la liste
@Composable
fun GroupList(
    groups: List<Group>,
    emptyText: String,
    membersByGroup: Map<String, List<GroupMember>>,
    onGroupClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    myGroupIds: Set<String> = emptySet(),
    onJoin: ((Group) -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null
) {
    LazyColumn(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (groups.isEmpty()) {
            item { EmptyText(emptyText, centered = true) }
        }

        items(groups, key = { it.id }) { group ->
            GroupCard(
                group = group,
                memberEmojis = membersByGroup[group.id]?.map { it.emoji } ?: group.memberEmojis,
                onClick = { onGroupClick(group.id) },
                onJoinClick = if (onJoin != null && group.id !in myGroupIds) { { onJoin(group) } } else null
            )
        }

        if (footer != null) {
            item { footer() }
        }
    }
}

@Composable
fun GroupCard(
    group: Group,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onJoinClick: (() -> Unit)? = null,
    memberEmojis: List<String> = group.memberEmojis
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(FieldBg, MediumRoundedCornerShape)
            .border(1.dp, Color.White.copy(alpha = 0.08f), MediumRoundedCornerShape)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (group.emoji.isNotBlank()) "${group.name} ${group.emoji}" else group.name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.active_members, group.memberCount ?: 0),
                    color = GraySubtitle,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Aperçu des membres
            Text(
                text = memberEmojis.filter { it.isNotBlank() }.distinct().take(3).joinToString(""),
                fontSize = 20.sp
            )
        }

        // Bouton rejoindre (uniquement pour les groupes à découvrir)
        if (onJoinClick != null) {
            Spacer(modifier = Modifier.height(12.dp))
            AppButton(
                text = stringResource(R.string.join_group),
                onClick = onJoinClick,
                height = 40.dp,
                textStyle = MaterialTheme.typography.labelSmall
            )
        }
    }
}

// Ligne du classement
@Composable
fun MemberRow(rank: Int, member: GroupMember) {
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