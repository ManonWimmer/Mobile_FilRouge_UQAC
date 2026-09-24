package com.example.healthybet.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.healthybet.ui.theme.GrayLabel
import com.example.healthybet.ui.theme.GraySubtitle
import com.example.healthybet.ui.theme.GreenAccent
import com.example.healthybet.ui.theme.RedFail
import com.example.healthybet.ui.theme.FieldBg
import com.example.healthybet.ui.theme.HealthyBetTheme
import com.example.healthybet.ui.theme.HighRoundedCornerShape
import com.example.healthybet.ui.theme.LittleRoundedCornerShape
import com.example.healthybet.ui.theme.MediumRoundedCornerShape


data class FriendBet(
    val emoji: String, // todo: use profile image, values etc
    val name: String,
    val prediction: String,
    val success: Boolean
)

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    // todo: use profile values
    val userName = "Alexis"
    val streakDays = 5
    val friendBets = listOf(
        FriendBet("😐", "Sarah J.", "Y ARRIVERA (+100 pts)", success = true),
        FriendBet("😎", "Thomas L.", "ÉCHOUERA", success = false)
    )

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 16.dp)){

        // Header
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

            // Profile icon (todo: use profile image)
            Box(modifier = Modifier.size(44.dp).background(GreenAccent, CircleShape), contentAlignment = Alignment.Center) {
                Text(text = "😄", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)){

                // Text salut!
                Text(
                    text = "Salut, $userName !",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Text streaks
                Text(
                    text = "\uD83D\uDD25 $streakDays jours de streak", // (emoji feu)
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Notifications
            Box(modifier = Modifier.size(40.dp).background(FieldBg, MediumRoundedCornerShape), contentAlignment = Alignment.Center){
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)

                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Réveil de demain
        Box(modifier = Modifier.fillMaxWidth().background(FieldBg, HighRoundedCornerShape).border(1.dp, GreenAccent, HighRoundedCornerShape).padding(20.dp)) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    // Icon réveil
                    Icon(
                        imageVector = Icons.Filled.WatchLater,
                        contentDescription = "Icon réveil",
                        tint = GreenAccent,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Text réveil de demain
                    Text(
                        text = "RÉVEIL DE DEMAIN",
                        color = GreenAccent,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.weight(1f)
                    )

                    // Text Temps restant
                    Box(modifier = Modifier.background(GreenAccent.copy(alpha = 0.15f), LittleRoundedCornerShape).padding(horizontal = 10.dp, vertical = 4.dp)) {
                        Text(
                            text = "- 08h 12m",
                            color = GreenAccent,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Heure du reveil
                Text(
                    text = "06:30",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.displaySmall
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Text Objectif réveil
                Text(
                    text = "\uD83C\uDFC3 Courir 5km avant 07:15", // (emoji courir)
                    color = GraySubtitle,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(14.dp))

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                Spacer(modifier = Modifier.height(14.dp))

                // Reveil enjeu
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

        Spacer(modifier = Modifier.height(24.dp))

        // Text paris des potes
        Text(
            text = "Pari de tes potes sur toi",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Paris affichés
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            friendBets.forEach { bet ->
                FriendBetCard(bet = bet, modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Button add réveil
        Button(
            onClick = { /* todo */ },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = LittleRoundedCornerShape,
            colors = ButtonDefaults.buttonColors(containerColor = GreenAccent)
        ) {
            Text(
                text = "+  Nouveau Réveil / Pari",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun FriendBetCard(bet: FriendBet, modifier: Modifier = Modifier) {
    Column(modifier = modifier.background(FieldBg, MediumRoundedCornerShape).padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            // Friend icon
            Box(modifier = Modifier.size(32.dp).background(Color.White.copy(alpha = 0.08f), CircleShape), contentAlignment = Alignment.Center) {
                Text(text = bet.emoji, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Friend name
            Text(
                text = bet.name,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Pari prediction (+ couleur fail)
        Box(
            modifier = Modifier.fillMaxWidth().background(if (bet.success) GreenAccent.copy(alpha = 0.15f) else RedFail.copy(alpha = 0.15f), LittleRoundedCornerShape).padding(vertical = 6.dp),) {
            Text(
                text = bet.prediction,
                color = if (bet.success) GreenAccent else RedFail,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
