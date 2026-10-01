package com.example.healthybet.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.healthybet.ui.theme.FieldBg
import com.example.healthybet.ui.theme.GrayLabel
import com.example.healthybet.ui.theme.GraySubtitle
import com.example.healthybet.ui.theme.GreenAccent
import com.example.healthybet.ui.theme.HealthyBetTheme
import com.example.healthybet.ui.theme.HighRoundedCornerShape
import com.example.healthybet.ui.theme.LittleRoundedCornerShape
import com.example.healthybet.ui.theme.MediumRoundedCornerShape

enum class HealthObjective(val emoji: String, val label: String) {
    Course("\uD83C\uDFC3", "Course"),
    Pas("\uD83D\uDC5F", "Pas"),
    Gym("\uD83C\uDFCB\uFE0F", "Gym"),
    Sommeil("\uD83D\uDE34", "Sommeil"),
    Calories("\uD83D\uDD25", "Calories")
}

enum class StakeType { Gage, Points }

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class) // pour rememberTimePickerState
@Composable
fun CreateAlarmScreen(
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {}) {

    // todo: lier à un ViewModel

    val timeState = rememberTimePickerState(initialHour = 6, initialMinute = 30, is24Hour = true)
    var objective by remember { mutableStateOf(HealthObjective.Course) }
    var target by remember { mutableStateOf("") }
    var stakeType by remember { mutableStateOf(StakeType.Gage) }
    var stake by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        // Header
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Créer un Réveil",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            // Bouton fermer
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Fermer",
                tint = GraySubtitle,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onClose() }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Heure du réveil
        Column(modifier = Modifier.fillMaxWidth().background(FieldBg, HighRoundedCornerShape).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            // Text heure du reveil
            Text(
                text = "HEURE DU RÉVEIL",
                color = GraySubtitle,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Selection de temps avec cadrant
            TimePicker(
                state = timeState,
                colors = TimePickerDefaults.colors(
                    timeSelectorSelectedContainerColor = GreenAccent.copy(alpha = 0.15f),
                    timeSelectorUnselectedContainerColor = Color.White.copy(alpha = 0.06f),
                    timeSelectorSelectedContentColor = GreenAccent,
                    timeSelectorUnselectedContentColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Text Objectif santé
        Text(
            text = "OBJECTIF SANTÉ",
            color = GraySubtitle,
            style = MaterialTheme.typography.labelMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Layour de gauche à droite avec size différents + wrap next line
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            HealthObjective.entries.forEach { item ->
                ObjectiveChip(
                    objective = item,
                    selected = item == objective,
                    onClick = { objective = item }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Text Valeur cible & heure limite
        Text(
            text = "Valeur cible & Heure limite",
            color = GraySubtitle,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        AlarmTextField(
            value = target,
            onValueChange = { target = it },
            placeholder = "5 km avant 07:15"
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Enjeu / contrat du pari
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(FieldBg, HighRoundedCornerShape)
                .padding(16.dp)
        ) {
            Text(
                text = "ENJEU / CONTRAT DU PARI",
                color = GraySubtitle,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Button gage soit physique soit points
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StakeTypeButton(
                    text = "Un gage physique",
                    selected = stakeType == StakeType.Gage,
                    onClick = { stakeType = StakeType.Gage },
                    modifier = Modifier.weight(1f)
                )
                StakeTypeButton(
                    text = "Points (ex: 200 pts)",
                    selected = stakeType == StakeType.Points,
                    onClick = { stakeType = StakeType.Points },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // todo: faire mieux plus tard pour avoir des trucs différents en fonction plutot qu'une zone de texte pour les 2
            // genre les points avoir un truc avec que des int etc..., gages liste prédéfinie jsp ?

            // Text précision du gage
            Text(
                text = "Précise le gage ou l'enjeu",
                color = GraySubtitle,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Text field gage
            AlarmTextField(
                value = stake,
                onValueChange = { stake = it },
                placeholder = if (stakeType == StakeType.Gage) "Offrir un pack de bières artisanales" else "200 pts"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bouton programmer réveil
        Button(
            onClick = { /* todo */},
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = LittleRoundedCornerShape,
            colors = ButtonDefaults.buttonColors(containerColor = GreenAccent)
        ) {
            Text(
                text = "Programmer mon réveil \u23F0",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

// Encadré avec emoji + objective name sélectionné ou pas
@Composable
private fun ObjectiveChip(objective: HealthObjective, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        // Couleur diff selon selection
        modifier = modifier
            .background(
                if (selected) GreenAccent else FieldBg,
                LittleRoundedCornerShape
            )
            .border(
                1.dp,
                if (selected) GreenAccent else Color.White.copy(alpha = 0.1f),
                LittleRoundedCornerShape
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Text emoji
        Text(text = objective.emoji, style = MaterialTheme.typography.labelSmall)

        Spacer(modifier = Modifier.size(6.dp))

        // Text nom objectif
        Text(
            text = objective.label,
            color = if (selected) Color.Black else Color.White,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

// Bouton gage (soit physique soit points)
@Composable
private fun StakeTypeButton(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = LittleRoundedCornerShape,
        border = BorderStroke(1.dp, if (selected) GreenAccent else Color.White.copy(alpha = 0.1f)),
        // Couleurs diff selon selection
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) GreenAccent.copy(alpha = 0.15f) else Color.Transparent,
            contentColor = if (selected) GreenAccent else Color.White
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}

// Text field classique mais qui a une bordure de couleur quand selectionné
@Composable
private fun AlarmTextField(value: String, onValueChange: (String) -> Unit, placeholder: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = LittleRoundedCornerShape,
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
fun CreateAlarmScreenPreview() {
    HealthyBetTheme {
        CreateAlarmScreen()
    }
}