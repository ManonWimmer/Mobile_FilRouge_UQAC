package com.example.healthybet.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthybet.ui.theme.FieldBg
import com.example.healthybet.ui.theme.GraySubtitle
import com.example.healthybet.ui.theme.GreenAccent
import com.example.healthybet.ui.theme.HealthyBetTheme
import com.example.healthybet.ui.theme.HighRoundedCornerShape
import com.example.healthybet.ui.theme.LittleRoundedCornerShape
import com.example.healthybet.ui.theme.MediumRoundedCornerShape
import com.example.healthybet.ui.theme.RedFail

data class Alarm(
    val time: String, // todo: use profile image, values etc
    val status: String,
    val success : Boolean,
    val finished : Boolean,
    val objective: String,
    val reward: String,
    // todo: parieurs
)

@Composable
fun AlarmsScreen(modifier: Modifier = Modifier){

    val streakDays = 5 // todo: use profile value
    val alarms = listOf(
        Alarm("06:30", "EN COURS", true, false, "\uD83C\uDFC3 Courir 5km avant 07:15", "Pack de bières"),
        Alarm("07:00", "RÉUSSI ✓", true, true,"\uD83D\uDEB6 10 000 pas avant 12:00", "200 Points"),
        Alarm("06:00", "ÉCHOUÉ ✗", false, true, "\uD83C\uDFCB\uFE0F 45 min de CrossFit avant 7h30", "Faire le ménage"),
    )

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 16.dp)){

        // Header
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

            Column(modifier = Modifier.weight(1f)){
                // Text tes réveil
                Text(
                    text = "Tes Réveils",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Text streak
            Box(modifier = Modifier.background(GreenAccent.copy(alpha = 0.15f), MediumRoundedCornerShape).padding(PaddingValues(10.dp)), contentAlignment = Alignment.Center){
                Text(
                    text = "\uD83D\uDD25 $streakDays Streak", // (emoji feu)
                    color = GreenAccent,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Réveils
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            alarms.forEach { alarm ->
                AlarmCard(alarm = alarm, modifier = Modifier.then(if (!alarm.finished) { Modifier.border(1.dp, GreenAccent, MediumRoundedCornerShape) } else { Modifier }))
            }
        }
    }
}

@Composable
private fun AlarmCard(alarm: Alarm, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().background(FieldBg, MediumRoundedCornerShape).padding(14.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            // Alarm time
            Text(
                text = alarm.time,
                color = Color.White,
                style = MaterialTheme.typography.displayMedium
            )

            // Status à droite
            Spacer(modifier = Modifier.weight(1f))

            // Alarm status badge
            Box(modifier = Modifier.background(if (alarm.success) GreenAccent.copy(alpha = 0.15f) else RedFail.copy(alpha = 0.15f), LittleRoundedCornerShape).padding(horizontal = 10.dp, vertical = 6.dp)) {
                Text(
                    text = alarm.status,
                    color = if (alarm.success) GreenAccent else RedFail,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Text Objectif réveil
        Text(
            text = alarm.objective,
            color = GraySubtitle,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(14.dp))

        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

        Spacer(modifier = Modifier.height(14.dp))

        // Reveil enjeu
        // todo: parieurs
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Enjeu : Un pack de bières artisanales",
                color = GraySubtitle,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
            Text(text = "\uD83D\uDE0F\uD83D\uDE0F\uD83D\uDE0F", fontSize = 14.sp) // (emojis personnes)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AlarmsScreenPreview() {
    HealthyBetTheme {
        AlarmsScreen()
    }
}