package com.grman.healthybet.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.grman.healthybet.R
import com.grman.healthybet.data.alarms.AlarmRepository
import com.grman.healthybet.data.members.rememberMemberProfile
import com.grman.healthybet.items.common.AppOutlinedButton
import com.grman.healthybet.items.EmojiAvatar
import com.grman.healthybet.items.StatCard
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.HealthyBetTheme
import com.grman.healthybet.ui.theme.RedFail

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
    val user = FirebaseAuth.getInstance().currentUser
    val profile = rememberMemberProfile()

    var points by remember { mutableIntStateOf(0) }
    var alarmsDone by remember { mutableIntStateOf(0) }

    // Statistiques en temps réel depuis les alarmes Firestore
    DisposableEffect(user?.uid) {
        val listener = user?.uid?.let {
            AlarmRepository.observeUserStats(it) { totalPoints, successfulAlarms ->
                points = totalPoints
                alarmsDone = successfulAlarms
            }
        }
        onDispose { listener?.remove() }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.profile_title),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        EmojiAvatar(emoji = profile.emoji, size = 96.dp, fontSize = 44.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = profile.username, color = Color.White, style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = user?.email.orEmpty(),
            color = GraySubtitle,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                value = points.toString(),
                label = stringResource(R.string.profile_points),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = alarmsDone.toString(),
                label = stringResource(R.string.profile_succesful_alarms),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        AppOutlinedButton(
            text = stringResource(R.string.log_out),
            onClick = {
                FirebaseAuth.getInstance().signOut() // déco Firebase
                onLogout() // retour welcome screen
            },
            color = RedFail,
            height = 52.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    HealthyBetTheme {
        ProfileScreen()
    }
}