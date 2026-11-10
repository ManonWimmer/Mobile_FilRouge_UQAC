package com.grman.healthybet.screens.groups

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.grman.healthybet.items.common.AppButton
import com.grman.healthybet.items.common.EmojiPicker
import com.grman.healthybet.items.common.FieldCard
import com.grman.healthybet.items.common.LabeledTextField
import com.grman.healthybet.items.common.ScreenHeader
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.HealthyBetTheme
import com.grman.healthybet.ui.theme.RedFail
import com.grman.healthybet.viewmodels.GroupsViewModel

private val groupEmojis = listOf("🏃", "🏋️", "👟", "🌅", "🔥", "💪", "🏅", "🚴", "🧘", "🥇")

@Composable
fun CreateGroupScreen(
    modifier: Modifier = Modifier,
    viewModel: GroupsViewModel,
    onClose: () -> Unit = {},
    onGroupCreated: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf(groupEmojis.first()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        ScreenHeader(title = stringResource(R.string.create_group), onClose = onClose)

        Spacer(modifier = Modifier.height(20.dp))

        // Icône du groupe
        Text(
            text = stringResource(R.string.group_icon),
            color = GraySubtitle,
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.height(10.dp))
        EmojiPicker(emojis = groupEmojis, selected = emoji, onSelect = { emoji = it })

        Spacer(modifier = Modifier.height(20.dp))

        // Infos du groupe
        FieldCard {
            Text(
                text = stringResource(R.string.group_infos),
                color = GraySubtitle,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            LabeledTextField(
                label = stringResource(R.string.group_name),
                value = name,
                onValueChange = { if (it.length <= 30) name = it },
                placeholder = stringResource(R.string.group_name_example)
            )

            Spacer(modifier = Modifier.height(14.dp))

            LabeledTextField(
                label = stringResource(R.string.group_description),
                value = description,
                onValueChange = { if (it.length <= 120) description = it },
                placeholder = stringResource(R.string.group_description_example),
                singleLine = false
            )
        }

        // Message d'erreur
        viewModel.errorMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = it, color = RedFail, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(24.dp))

        AppButton(
            text = stringResource(R.string.create_group),
            onClick = { viewModel.createGroup(name, emoji, description) { onGroupCreated() } },
            enabled = name.isNotBlank() && !viewModel.isLoading,
            loading = viewModel.isLoading,
            height = 52.dp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
fun CreateGroupScreenPreview() {
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
        CreateGroupScreen(viewModel = GroupsViewModel(fakeRepo))
    }
}