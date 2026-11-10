package com.grman.healthybet.screens.groups

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.grman.healthybet.R
import com.grman.healthybet.data.groups.GroupMember
import com.grman.healthybet.data.groups.GroupRepository
import com.grman.healthybet.data.groups.GroupsOverview
import com.grman.healthybet.items.common.AppOutlinedButton
import com.grman.healthybet.items.common.AppTextField
import com.grman.healthybet.items.groups.GroupList
import com.grman.healthybet.items.common.ScreenHeader
import com.grman.healthybet.items.common.SegmentedTabs
import com.grman.healthybet.items.common.SquareIconButton
import com.grman.healthybet.ui.theme.GrayLabel
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.HealthyBetTheme
import com.grman.healthybet.viewmodels.GroupsViewModel

@Composable
fun GroupsScreen(
    modifier: Modifier = Modifier,
    viewModel: GroupsViewModel,
    onCreateGroupClick: () -> Unit = {},
    onGroupClick: (String) -> Unit = {}
) {
    // Mise à jour de l'affichage des groupes à chaque apparition de l'écran
    LaunchedEffect(Unit) { viewModel.loadGroups() }

    val allGroupIds = viewModel.myGroups.map { it.id } + viewModel.discoverGroups.map { it.id }
    LaunchedEffect(allGroupIds) { viewModel.loadMembersForGroups(allGroupIds) }

    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {

        ScreenHeader(title = stringResource(R.string.groups)) {
            SquareIconButton(
                icon = Icons.Filled.Add,
                description = stringResource(R.string.create_group),
                onClick = onCreateGroupClick,
                containerColor = GreenAccent.copy(alpha = 0.15f),
                tint = GreenAccent
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        SegmentedTabs(
            tabs = listOf(stringResource(R.string.my_groups), stringResource(R.string.discover)),
            selectedIndex = selectedTab,
            onSelect = { selectedTab = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> GroupList(
                groups = viewModel.myGroups,
                emptyText = stringResource(R.string.no_groups),
                membersByGroup = viewModel.membersByGroup,
                onGroupClick = onGroupClick,
                modifier = Modifier.weight(1f),
                footer = {
                    AppOutlinedButton(
                        text = stringResource(R.string.create_new_group),
                        onClick = onCreateGroupClick,
                        color = GreenAccent,
                        height = 52.dp
                    )
                }
            )
            else -> SearchGroupContent(
                viewModel = viewModel,
                onGroupClick = onGroupClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SearchGroupContent(viewModel: GroupsViewModel, onGroupClick: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {

        // Barre de recherche
        AppTextField(
            value = viewModel.searchQuery,
            onValueChange = { viewModel.searchQuery = it },
            placeholder = stringResource(R.string.search_group),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.search),
                    tint = GrayLabel
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(if (viewModel.searchQuery.isBlank()) R.string.all_groups else R.string.results),
            color = GraySubtitle,
            style = MaterialTheme.typography.labelMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        GroupList(
            groups = viewModel.filteredDiscover,
            emptyText = stringResource(R.string.no_group_found),
            membersByGroup = viewModel.membersByGroup,
            onGroupClick = onGroupClick,
            myGroupIds = viewModel.myGroups.map { it.id }.toSet(),
            onJoin = { viewModel.joinGroup(it.id) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
fun GroupsScreenPreview() {
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
        GroupsScreen(viewModel = GroupsViewModel(fakeRepo))
    }
}