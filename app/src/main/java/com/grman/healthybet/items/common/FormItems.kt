package com.grman.healthybet.items.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.grman.healthybet.R
import com.grman.healthybet.data.groups.Group
import com.grman.healthybet.ui.theme.FieldBg
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.HighRoundedCornerShape
import com.grman.healthybet.ui.theme.LittleRoundedCornerShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.grman.healthybet.ui.theme.GrayLabel
import com.grman.healthybet.ui.theme.MediumRoundedCornerShape

// Carte grise qui contient un champ de formulaire
@Composable
fun FieldCard(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(FieldBg, HighRoundedCornerShape)
            .padding(16.dp),
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}

// Carte avec un label, la valeur choisie (en vert) et un slider
@Composable
fun SliderCard(
    label: String,
    valueText: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int
) {
    FieldCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                color = GraySubtitle,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = valueText,
                color = GreenAccent,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = GreenAccent,
                activeTrackColor = GreenAccent,
                inactiveTrackColor = Color.White.copy(alpha = 0.1f)
            )
        )
    }
}

// Fond + bordure verts quand l'élément est sélectionné
private fun Modifier.selectableStyle(selected: Boolean, shape: Shape) = this
    .background(if (selected) GreenAccent.copy(alpha = 0.15f) else FieldBg, shape)
    .border(1.dp, if (selected) GreenAccent else Color.White.copy(alpha = 0.1f), shape)

@Composable
fun GroupChip(label: String, onClick: () -> Unit, selected: Boolean = false) {
    Text(
        text = label,
        color = if (selected) GreenAccent else Color.White,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier
            .selectableStyle(selected, LittleRoundedCornerShape)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    )
}

// Grille d'emojis à choisir
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmojiPicker(emojis: List<String>, selected: String, onSelect: (String) -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        emojis.forEach { emoji ->
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .selectableStyle(emoji == selected, MediumRoundedCornerShape)
                    .clickable { onSelect(emoji) },
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

// Text field avec bordure verte quand il est sélectionné
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    isPassword: Boolean = false,
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
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
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

// Label gris + champ texte
// (dans une Column : le label reste aligné à gauche même si le parent centre ses enfants)
@Composable
fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = GraySubtitle, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(8.dp))
        AppTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            singleLine = singleLine,
            isPassword = isPassword
        )
    }
}

// Label + liste de chips pour choisir un groupe
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GroupPicker(groups: List<Group>, onSelect: (String) -> Unit) {
    Text(
        text = stringResource(R.string.group),
        color = GraySubtitle,
        style = MaterialTheme.typography.bodySmall
    )
    Spacer(modifier = Modifier.height(8.dp))

    if (groups.isEmpty()) {
        Text(
            text = stringResource(R.string.join_group_alarm),
            color = GraySubtitle,
            style = MaterialTheme.typography.bodyMedium
        )
    } else {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            groups.forEach { group ->
                GroupChip(
                    label = stringResource(R.string.group_label, group.emoji, group.name),
                    onClick = { onSelect(group.id) }
                )
            }
        }
    }
}