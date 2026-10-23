package com.grman.healthybet.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.grman.healthybet.ui.theme.FieldBg
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.HealthyBetTheme
import com.grman.healthybet.ui.theme.LittleRoundedCornerShape
import com.grman.healthybet.ui.theme.MediumRoundedCornerShape
import com.grman.healthybet.ui.theme.RedFail

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
    val user = FirebaseAuth.getInstance().currentUser
    var emoji by remember { mutableStateOf("😄") } // par défaut
    var username by remember { mutableStateOf(user?.displayName ?: "Username introuvable") }

    // Récupère emoji + pseudo dans members/{uid}
    LaunchedEffect(user?.uid) {
        val uid = user?.uid ?: return@LaunchedEffect
        FirebaseFirestore.getInstance().collection("members").document(uid).get()
            .addOnSuccessListener { doc ->
                emoji = doc.getString("emoji") ?: "😄"
                username = doc.getString("username") ?: username
            }
    }

    // todo: vraies stats depuis la bdd
    val streakDays = 5
    val points = 1200
    val alarmsDone = 12

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Titre
        Text(
            text = "Profil",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Emoji
        Box(
            modifier = Modifier.size(96.dp).background(GreenAccent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 44.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Pseudo
        Text(
            text = username,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Email
        Text(
            text = user?.email ?: "",
            color = GraySubtitle,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Stats
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(value = "\uD83D\uDD25 $streakDays", label = "Streak", modifier = Modifier.weight(1f))
            StatCard(value = "$points", label = "Points", modifier = Modifier.weight(1f))
            StatCard(value = "$alarmsDone", label = "Réveils réussis", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Déconnexion
        OutlinedButton(
            onClick = {
                FirebaseAuth.getInstance().signOut() // deco firebase
                onLogout() // retour welcome screejn
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = LittleRoundedCornerShape,
            border = BorderStroke(1.dp, RedFail),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = RedFail)
        ) {
            Text(text = "Se déconnecter", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.background(FieldBg, MediumRoundedCornerShape).padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Valeur stat
        Text(text = value, color = Color.White, style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(4.dp))

        // Nom stat
        Text(text = label, color = GraySubtitle, style = MaterialTheme.typography.labelSmall)
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    HealthyBetTheme {
        ProfileScreen()
    }
}