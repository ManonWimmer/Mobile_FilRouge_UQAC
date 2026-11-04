package com.grman.healthybet.alarm

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.grman.healthybet.data.alarms.AlarmRepository
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.HealthyBetTheme
import androidx.compose.material3.MaterialTheme
import com.grman.healthybet.R
import com.grman.healthybet.data.alarms.objectiveText

class AlarmRingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        val alarmId = intent.getStringExtra("alarmId")
        val stepGoal = intent.getIntExtra("stepGoal", 0)
        val durationMinutes = intent.getIntExtra("durationMinutes", 0)

        // Textes
        val title = getString(R.string.ring_wake_up)
        val objective = getString(R.string.objective_description, objectiveText(this, stepGoal, durationMinutes))
        val stopLabel = getString(R.string.ring_stop)

        setContent {
            HealthyBetTheme {
                Column(
                    modifier = Modifier.fillMaxSize().background(Color.Black).padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = objective,
                        color = Color.White
                    )

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = {
                            startService(
                                Intent(this@AlarmRingActivity, AlarmService::class.java)
                                    .setAction(AlarmService.ACTION_STOP)
                            )
                            //alarmId?.let { AlarmRepository.onAlarmStopped(it, FirebaseAuth.getInstance().currentUser?.uid) }
                            finish()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenAccent),
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text(
                            text = stopLabel
                        )
                    }
                }
            }
        }
    }
}