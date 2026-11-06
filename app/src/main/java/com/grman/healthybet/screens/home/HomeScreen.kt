package com.grman.healthybet.screens.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.RedFail
import com.grman.healthybet.ui.theme.FieldBg
import com.grman.healthybet.ui.theme.HealthyBetTheme
import com.grman.healthybet.ui.theme.HighRoundedCornerShape
import com.grman.healthybet.ui.theme.LittleRoundedCornerShape
import com.grman.healthybet.ui.theme.MediumRoundedCornerShape
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.grman.healthybet.R
import com.grman.healthybet.alarm.AlarmScheduler.nextTriggerMillis
import com.grman.healthybet.data.alarms.AlarmData
import com.grman.healthybet.data.alarms.AlarmRepository
import com.grman.healthybet.data.health.StepsReader
import com.grman.healthybet.items.AlarmCard
import kotlinx.coroutines.delay

data class FriendBet(
    val emoji: String,
    val name: String,
    val prediction: String,
    val success: Boolean
)

@Composable
fun HomeScreen(modifier: Modifier = Modifier,
               onCreateAlarmClick : () -> Unit = {},
               onSelectAlarmScreen : () -> Unit = {}) {

    val user = FirebaseAuth.getInstance().currentUser

    val defaultUsername = stringResource(R.string.username_not_found)
    var username by remember { mutableStateOf(user?.displayName ?: defaultUsername) }

    val defaultEmoji = stringResource(R.string.default_emoji)
    var emoji by remember { mutableStateOf(defaultEmoji) }

    val uid = user?.uid

    // Alarms de l'user
    var alarms by remember { mutableStateOf<List<AlarmData>>(emptyList()) }
    DisposableEffect(uid) {
        val listener = uid?.let { id -> AlarmRepository.observeUserAlarms(id) { alarms = it } }
        onDispose { listener?.remove() }
    }

    // temps actuel rafraîchi toutes les 30 s pour avoir la prochaine alarme
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = System.currentTimeMillis()
            delay(30_000)
        }
    }

    // pochain réveil : actif + la sonnerie pas déclenchée
    val nextAlarm = alarms
        .filter { it.enabled && uid != null && uid !in it.results.keys }
        .minByOrNull { it.nextTriggerMillis(now) }

    // Health connect permission
    val healthPermissionLauncher = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { /* refus : les pas ne seront pas comptés */ }

    // Get emoji
    LaunchedEffect(Unit) {
        val uid = user?.uid ?: return@LaunchedEffect
        FirebaseFirestore.getInstance().collection("members").document(uid).get()
            .addOnSuccessListener { doc ->
                emoji = doc.getString("emoji") ?: defaultEmoji
            }
    }

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 16.dp)){

        // Header
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

            // Profile icon
            Box(modifier = Modifier.size(44.dp).background(GreenAccent, CircleShape), contentAlignment = Alignment.Center) {
                Text(text = emoji, style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)){

                // Text salut!
                Text(
                    text = stringResource(R.string.greeting, username),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Prochain réveil
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(1f)
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
            Text(
                text = stringResource(R.string.no_next_alarm),
                color = GraySubtitle,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Button add réveil
        Button(
            onClick = onCreateAlarmClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = LittleRoundedCornerShape,
            colors = ButtonDefaults.buttonColors(containerColor = GreenAccent)
        ) {
            Text(
                text = stringResource(R.string.alarm_new),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HealthyBetTheme {
        HomeScreen()
    }
}
