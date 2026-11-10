package com.grman.healthybet.screens.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.grman.healthybet.R
import com.grman.healthybet.alarm.AlarmScheduler.nextTriggerMillis
import com.grman.healthybet.data.alarms.AlarmData
import com.grman.healthybet.data.alarms.AlarmRepository
import com.grman.healthybet.data.members.rememberMemberProfile
import com.grman.healthybet.items.alarms.AlarmCard
import com.grman.healthybet.items.common.AppButton
import com.grman.healthybet.items.EmojiAvatar
import com.grman.healthybet.items.common.EmptyText
import com.grman.healthybet.items.alarms.AlarmCard
import com.grman.healthybet.items.common.AppButton
import com.grman.healthybet.items.common.EmptyText
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.HealthyBetTheme
import com.grman.healthybet.ui.theme.MediumRoundedCornerShape
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onCreateAlarmClick: () -> Unit = {},
    onSelectAlarmScreen: () -> Unit = {}
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val profile = rememberMemberProfile()

    // Alarmes de l'user
    var alarms by remember { mutableStateOf<List<AlarmData>>(emptyList()) }
    DisposableEffect(uid) {
        val listener = uid?.let { id -> AlarmRepository.observeUserAlarms(id) { alarms = it } }
        onDispose { listener?.remove() }
    }

    // Temps actuel rafraîchi toutes les 30 s pour avoir la prochaine alarme
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = System.currentTimeMillis()
            delay(30_000)
        }
    }

    // Prochain réveil : actif + sonnerie pas encore déclenchée
    val nextAlarm = alarms
        .filter { it.enabled && uid != null && uid !in it.results.keys }
        .minByOrNull { it.nextTriggerMillis(now) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header : avatar + salutation
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            EmojiAvatar(emoji = profile.emoji)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.greeting, profile.username),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Titre « Prochain réveil » (avec icône)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.WatchLater,
                contentDescription = stringResource(R.string.alarm_icon_description),
                tint = GreenAccent,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.alarm_next),
                color = GreenAccent,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (nextAlarm != null) {
            AlarmCard(
                alarm = nextAlarm,
                result = null, // pas encore sonné
                onClick = onSelectAlarmScreen,
                modifier = Modifier.border(1.dp, GreenAccent, MediumRoundedCornerShape)
            )
        } else {
            EmptyText(stringResource(R.string.no_next_alarm))
        }

        Spacer(modifier = Modifier.height(24.dp))

        AppButton(
            text = stringResource(R.string.alarm_new),
            onClick = onCreateAlarmClick,
            height = 52.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HealthyBetTheme {
        HomeScreen()
    }
}