package com.grman.healthybet.screens.alarms

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.grman.healthybet.R
import com.grman.healthybet.alarm.AlarmScheduler
import com.grman.healthybet.data.alarms.AlarmData
import com.grman.healthybet.data.alarms.AlarmRepository
import com.grman.healthybet.data.alarms.AlarmState
import com.grman.healthybet.data.alarms.formatDuration
import com.grman.healthybet.data.alarms.roundStepGoal
import com.grman.healthybet.data.groups.GroupMember
import com.grman.healthybet.data.groups.GroupRepository
import com.grman.healthybet.data.groups.GroupsOverview
import com.grman.healthybet.items.common.AppButton
import com.grman.healthybet.items.common.FieldCard
import com.grman.healthybet.items.common.GroupPicker
import com.grman.healthybet.items.common.ScreenHeader
import com.grman.healthybet.items.common.SliderCard
import com.grman.healthybet.ui.theme.FieldBg
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.HealthyBetTheme
import com.grman.healthybet.viewmodels.GroupsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("LocalContextGetResourceValueCall") // warning relou à l'utilisation de string resource au toast make test
@OptIn(ExperimentalMaterial3Api::class) // pour rememberTimePickerState
@Composable
fun CreateAlarmScreen(
    modifier: Modifier = Modifier,
    groupsViewModel: GroupsViewModel,
    initialGroupId: String = "",
    onClose: () -> Unit = {}
) {
    val context = LocalContext.current

    // Charge mes groupes
    LaunchedEffect(Unit) { groupsViewModel.loadGroups() }

    // todo: lier à un ViewModel
    var selectedGroupId by remember { mutableStateOf(initialGroupId) }
    val timeState = rememberTimePickerState(initialHour = 6, initialMinute = 30, is24Hour = true)
    var stepGoal by remember { mutableFloatStateOf(5000f) }
    var durationMinutes by remember { mutableFloatStateOf(60f) }

    val notifPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* gérer le refus */ }

    // Textes récupérés ici car programmer() n'est pas Composable
    val loginFirst = stringResource(R.string.login_first)
    val allowExactAlarms = stringResource(R.string.allow_exact_alarms)
    val scheduledTemplate = stringResource(R.string.alarm_scheduled)

    fun programmer() {
        if (selectedGroupId.isBlank()) return

        // Permission notifications (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        val alarm = AlarmData(
            groupId = selectedGroupId,
            hour = timeState.hour,
            minute = timeState.minute,
            stepGoal = roundStepGoal(stepGoal.toInt()), // arrondi à 500 pcq des fois c 499
            durationMinutes = durationMinutes.toInt(),
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
        ScreenHeader(title = stringResource(R.string.create_alarm), onClose = onClose)

        Spacer(modifier = Modifier.height(20.dp))

        // Heure du réveil
        FieldCard(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.alarm_hour),
                color = GraySubtitle,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
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

        // Nombre de pas : 1 000 à 20 000, paliers de 500
        SliderCard(
            label = stringResource(R.string.step_goal),
            valueText = stringResource(R.string.step_goal_value, roundStepGoal(stepGoal.toInt())),
            value = stepGoal,
            onValueChange = { stepGoal = it },
            valueRange = 1000f..20000f,
            steps = 37
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Temps pour y arriver : 1h à 24h, paliers de 1h
        SliderCard(
            label = stringResource(R.string.alarm_duration),
            valueText = formatDuration(context, durationMinutes.toInt()),
            value = durationMinutes,
            onValueChange = { durationMinutes = it },
            valueRange = 60f..1440f,
            steps = 23
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Sélection du groupe (masquée une fois un groupe choisi)
        if (selectedGroupId.isBlank()) {
            GroupPicker(
                groups = groupsViewModel.myGroups,
                onSelect = { selectedGroupId = it }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bouton de test
        AppButton(
            text = stringResource(R.string.test_alarm),
            onClick = {
                stepGoal = 10f
                durationMinutes = 5f
            },
            containerColor = FieldBg,
            contentColor = GreenAccent
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Bouton programmer réveil
        AppButton(
            text = stringResource(R.string.set_alarm),
            onClick = { programmer() },
            enabled = selectedGroupId.isNotBlank(),
            height = 52.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateAlarmScreenPreview() {
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
        CreateAlarmScreen(groupsViewModel = GroupsViewModel(fakeRepo))
    }
}