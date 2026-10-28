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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import android.app.AlarmManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.grman.healthybet.alarm.AlarmScheduler
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class) // pour rememberTimePickerState
@Composable
fun CreateAlarmScreen(
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {}) {

    val context = LocalContext.current

    val notifPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* gérer le refus */ }

    // todo: lier à un ViewModel

    val timeState = rememberTimePickerState(initialHour = 6, initialMinute = 30, is24Hour = true)
    var target by remember { mutableStateOf("") }

    fun programmer() {
        //Permission notifications (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        //Planification
        val trigger = AlarmScheduler.schedule(
            context = context,
            hour = timeState.hour,
            minute = timeState.minute,
            objective = target,
        )

        if (trigger == null) {
            //Permission "alarmes exactes" manquante = ouvrir les parametres
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.startActivity(
                    Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:${context.packageName}"))
                )
            }
            Toast.makeText(context, "Autorise les alarmes exactes puis réessaie", Toast.LENGTH_LONG).show()
        } else {
            val heure = SimpleDateFormat("EEE HH:mm", Locale.FRANCE).format(Date(trigger)) //J'ai aucune idée de ce que c'est ici
            Toast.makeText(context, "Réveil programmé : $heure", Toast.LENGTH_SHORT).show()
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
                text = "Créer un Réveil",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            // Bouton fermer
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Fermer",
                tint = GraySubtitle,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onClose() }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Heure du réveil
        Column(modifier = Modifier.fillMaxWidth().background(FieldBg, HighRoundedCornerShape).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            // Text heure du reveil
            Text(
                text = "HEURE DU RÉVEIL",
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

        // Text Valeur cible & heure limite
        Text(
            text = "Heure limite",
            color = GraySubtitle,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        // To do : heure sélectionnable plus facilement
        AlarmTextField(
            value = target,
            onValueChange = { target = it },
            placeholder = "5 000 pas avant 07:15"
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Bouton programmer réveil
        Button(
            onClick = { programmer() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = LittleRoundedCornerShape,
            colors = ButtonDefaults.buttonColors(containerColor = GreenAccent)
        ) {
            Text(
                text = "Programmer mon réveil \u23F0",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
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
        CreateAlarmScreen()
    }
}