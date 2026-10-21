package com.grman.healthybet.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grman.healthybet.ui.theme.FieldBg
import com.grman.healthybet.ui.theme.GrayLabel
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.HealthyBetTheme
import com.grman.healthybet.ui.theme.HighRoundedCornerShape
import com.grman.healthybet.ui.theme.LittleRoundedCornerShape
import com.grman.healthybet.ui.theme.MediumRoundedCornerShape
import com.grman.healthybet.ui.theme.RedFail

private val groupEmojis = listOf("🏃", "🏋️", "👟", "🌅", "🔥", "💪", "🏅", "🚴", "🧘", "🥇") // emojis à choisiri

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

        // Header
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Créer un Groupe",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            // Bouton fermer
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Fermer",
                tint = GraySubtitle,
                modifier = Modifier.size(24.dp).clickable { onClose() }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Icône du groupe
        Text(
            text = "ICÔNE DU GROUPE",
            color = GraySubtitle,
            style = MaterialTheme.typography.labelMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Emojis
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            groupEmojis.forEach { item ->
                val selected = item == emoji
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(if (selected) GreenAccent.copy(alpha = 0.15f) else FieldBg, MediumRoundedCornerShape)
                        .border(
                            1.dp,
                            if (selected) GreenAccent else Color.White.copy(alpha = 0.1f),
                            MediumRoundedCornerShape
                        )
                        .clickable { emoji = item },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item, style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Infos du groupe
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(FieldBg, HighRoundedCornerShape)
                .padding(16.dp)
        ) {
            Text(
                text = "INFORMATIONS",
                color = GraySubtitle,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Nom du groupe",
                color = GraySubtitle,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            GroupTextField(
                value = name,
                onValueChange = { if (it.length <= 30) name = it },
                placeholder = "Les runners du matin"
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Description (optionnel)",
                color = GraySubtitle,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            GroupTextField(
                value = description,
                onValueChange = { if (it.length <= 120) description = it },
                placeholder = "Réveil avant 6h, on se motive !",
                singleLine = false
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Message d'erreur
        viewModel.errorMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = it, color = RedFail, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bouton créer
        Button(
            onClick = {
                viewModel.createGroup(name, emoji, description) { onGroupCreated() }
            },
            enabled = name.isNotBlank() && !viewModel.isLoading,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = LittleRoundedCornerShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = GreenAccent,
                disabledContainerColor = GreenAccent.copy(alpha = 0.3f)
            )
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = Color.Black)
            } else {
                Text(text = "Créer le groupe", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

// Text field partagé (création + recherche de groupe)
@Composable
internal fun GroupTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        shape = LittleRoundedCornerShape,
        leadingIcon = leadingIcon,
        placeholder = {
            Text(text = placeholder, color = GrayLabel, style = MaterialTheme.typography.bodyMedium)
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color.White.copy(alpha = 0.04f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.04f),
            focusedBorderColor = GreenAccent,
            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
            cursorColor = GreenAccent
        )
    )
}

@Preview(showBackground = true)
@Composable
fun CreateGroupScreenPreview() {
    HealthyBetTheme {
        CreateGroupScreen(viewModel = GroupsViewModel())
    }
}