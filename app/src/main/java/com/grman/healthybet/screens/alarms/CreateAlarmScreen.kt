package com.grman.healthybet.screens.alarms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.grman.healthybet.ui.theme.FieldBg
import com.grman.healthybet.ui.theme.GrayLabel
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.HealthyBetTheme
import com.grman.healthybet.ui.theme.HighRoundedCornerShape
import com.grman.healthybet.ui.theme.LittleRoundedCornerShape
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.grman.healthybet.R
import com.grman.healthybet.alarm.AlarmScheduler
import com.grman.healthybet.data.alarms.AlarmData
import com.grman.healthybet.data.alarms.AlarmRepository
import com.grman.healthybet.data.alarms.AlarmState
import com.grman.healthybet.data.alarms.roundStepGoal
import com.grman.healthybet.data.alarms.formatDuration
import com.grman.healthybet.items.CloseButton
import com.grman.healthybet.viewmodels.GroupsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@SuppressLint("LocalContextGetResourceValueCall") // warning relou à l'utilisation de string resource au toast make test
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class) // pour rememberTimePickerState
@Composable
fun CreateAlarmScreen(
    modifier: Modifier = Modifier,
    groupsViewModel: GroupsViewModel,
    initialGroupId: String = "",
    onClose: () -> Unit = {}) {

    val context = LocalContext.current

    // Charge mes groupes
    LaunchedEffect(Unit) { groupsViewModel.loadGroups() }

    var selectedGroupId by remember { mutableStateOf(initialGroupId) }

    val notifPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* gérer le refus */ }

    // todo: lier à un ViewModel

    val timeState = rememberTimePickerState(initialHour = 6, initialMinute = 30, is24Hour = true)
    var stepGoal by remember { mutableFloatStateOf(5000f) }
    var durationMinutes by remember { mutableFloatStateOf(60f) }

    // Recuperation des textes (pas possible dans programmer qui n'est pas Composable)
    val loginFirst = stringResource(R.string.login_first)
    val allowExactAlarms = stringResource(R.string.allow_exact_alarms)
    val scheduledTemplate = stringResource(R.string.alarm_scheduled)

    @SuppressLint("LocalContextGetResourceValueCall")
    fun programmer() {
        if (selectedGroupId.isBlank())
            return

        //Permission notifications (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        val steps = roundStepGoal(stepGoal.toInt()); // Arrondir à 500 pcq des fois c 499
        val minutes = durationMinutes.toInt()

        val alarm = AlarmData(
            groupId = selectedGroupId,

            hour = timeState.hour,
            minute = timeState.minute,

            stepGoal = steps,
            durationMinutes = minutes,

            advancement = AlarmState.IN_PROGRESS.name,

            success = false,
            enabled = true
        )

        AlarmRepository.create(alarm) { saved ->
            if (saved == null) {
                Toast.makeText(context, loginFirst, Toast.LENGTH_SHORT).show()
                return@create
            }
            val trigger = AlarmScheduler.schedule(context, saved)
            if (trigger == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    context.startActivity(
                        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:${context.packageName}"))
                    )
                }
                Toast.makeText(context, allowExactAlarms, Toast.LENGTH_LONG).show()
            } else {
                val heure = SimpleDateFormat("EEE HH:mm", Locale.FRANCE).format(Date(trigger))
                Toast.makeText(context, scheduledTemplate.format(heure), Toast.LENGTH_SHORT).show()
                onClose()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        // Header
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.create_alarm),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            // Bouton fermer
            CloseButton(onClick = onClose)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Heure du réveil
        Column(modifier = Modifier.fillMaxWidth().background(FieldBg, HighRoundedCornerShape).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            // Text heure du reveil
            Text(
                text = stringResource(R.string.alarm_hour),
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

        Spacer(modifier = Modifier.height(16.dp))

        // Nombre de pas
        Column(modifier = Modifier.fillMaxWidth().background(FieldBg, HighRoundedCornerShape).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.step_goal),
                    color = GraySubtitle,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.step_goal_value, roundStepGoal(stepGoal.toInt())),
                    color = GreenAccent,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Slider sélection pas
            Slider(
                value = stepGoal,
                onValueChange = { stepGoal = it },
                valueRange = 1000f..20000f, // 1 000 à 20 000 pas
                steps = 37, // paliers de 500 pas
                colors = SliderDefaults.colors(
                    thumbColor = GreenAccent,
                    activeTrackColor = GreenAccent,
                    inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Temps pour y arriver
        Column(modifier = Modifier.fillMaxWidth().background(FieldBg, HighRoundedCornerShape).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.alarm_duration),
                    color = GraySubtitle,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = formatDuration(LocalContext.current, durationMinutes.toInt()),
                    color = GreenAccent,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Slider(
                value = durationMinutes,
                onValueChange = { durationMinutes = it },
                valueRange = 60f..1440f, // 1h -> 24h
                steps = 23, // paliers de 1h
                colors = SliderDefaults.colors(
                    thumbColor = GreenAccent,
                    activeTrackColor = GreenAccent,
                    inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sélection du groupe
        Text(
            text = stringResource(R.string.group),
            color = GraySubtitle,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (groupsViewModel.myGroups.isEmpty()) {
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
                groupsViewModel.myGroups.forEach { group ->
                    GroupChip(
                        label = stringResource(R.string.group_label, group.emoji, group.name),
                        selected = selectedGroupId == group.id,
                        onClick = { selectedGroupId = group.id }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bouton programmer réveil
        Button(
            onClick = { programmer() },
            enabled = selectedGroupId.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = LittleRoundedCornerShape,
            colors = ButtonDefaults.buttonColors(containerColor = GreenAccent, disabledContainerColor = GreenAccent.copy(alpha = 0.3f))
        ) {
            Text(
                text = stringResource(R.string.set_alarm),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}


@Composable
private fun GroupChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        color = if (selected) GreenAccent else Color.White,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier
            .background(if (selected) GreenAccent.copy(alpha = 0.15f) else FieldBg, LittleRoundedCornerShape)
            .border(1.dp, if (selected) GreenAccent else Color.White.copy(alpha = 0.1f), LittleRoundedCornerShape)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    )
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
        CreateAlarmScreen(groupsViewModel = GroupsViewModel())
    }
}