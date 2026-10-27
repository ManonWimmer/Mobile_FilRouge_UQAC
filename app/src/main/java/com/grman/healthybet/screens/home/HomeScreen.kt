package com.grman.healthybet.screens.home

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
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
    val userName = user?.displayName ?: "Username introuvable"
    var emoji by remember { mutableStateOf("😄") } // par défaut

    // Get emoji
    LaunchedEffect(Unit) {
        val uid = user?.uid ?: return@LaunchedEffect
        FirebaseFirestore.getInstance().collection("members").document(uid).get()
            .addOnSuccessListener { doc ->
                emoji = doc.getString("emoji") ?: "😄"
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
                    text = "Salut, $userName !",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Réveil de demain
        // todo: utiliser code de alarms screen
        Box(modifier = Modifier.fillMaxWidth().background(FieldBg, HighRoundedCornerShape).clickable(onClick = onSelectAlarmScreen).border(1.dp, GreenAccent, HighRoundedCornerShape).padding(20.dp)) {
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
                    //TODO BUTTON SWAP DE SCREEN
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Heure du reveil
                Text(
                    text = "06:30",
                    color = Color.White,
                    style = MaterialTheme.typography.displayMedium
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Text Objectif réveil (todo: bdd)
                Text(
                    text = "\uD83C\uDFC3 Courir 5km avant 07:15", // (emoji courir)
                    color = GraySubtitle,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(14.dp))

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                Spacer(modifier = Modifier.height(14.dp))

                // Membre (todo: bdd)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "\uD83D\uDE0F\uD83D\uDE0F\uD83D\uDE0F", fontSize = 14.sp) // (emojis personnes)
                }
            }
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
                text = "+  Nouveau Réveil / Pari",
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
