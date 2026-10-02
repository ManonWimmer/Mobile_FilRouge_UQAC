package com.example.healthybet.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthybet.R
import com.example.healthybet.ui.theme.FieldBg
import com.example.healthybet.ui.theme.GrayLabel
import com.example.healthybet.ui.theme.GraySubtitle
import com.example.healthybet.ui.theme.HealthyBetTheme
import com.example.healthybet.ui.theme.GreenAccent
import com.example.healthybet.ui.theme.LittleRoundedCornerShape
import com.example.healthybet.ui.theme.MediumRoundedCornerShape

@Composable
fun SignUpScreen(modifier: Modifier = Modifier,
                 onAccountCreationClick: () -> Unit = {}) {

    val viewModel : SignUpViewModel = viewModel()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Column(modifier = Modifier.widthIn(min = 400.dp, max = 600.dp).align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {

            // Logo
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(GreenAccent, MediumRoundedCornerShape),
                contentAlignment = Alignment.Center
            ) {
                Image(painterResource(
                    id=R.drawable.cat),
                    contentDescription = "cat",
                    modifier = Modifier.size(20.dp)
                );
            }

            Spacer(modifier = Modifier.height(12.dp))

            // App name
            Text(
                text = "Healthy Bet",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Text 1
            Text(
                text = "Crée ton compte",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Text 2
            Text(
                text = "Rejoins l'arène et commence à te réveiller en forme",
                color = GraySubtitle,
                fontSize = 14.sp, // todo: use type
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Pseudo
            LabeledField(label = "Pseudo") {
                OutlinedTextField(
                    value = viewModel.pseudo,
                    onValueChange = { viewModel.pseudo = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = LittleRoundedCornerShape,
                    colors = fieldColors(),
                    singleLine = true,
                    placeholder = { Text("exemple de pseudo cool.tkt") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // EMail
            LabeledField(label = "Adresse Email") {
                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = LittleRoundedCornerShape,
                    colors = fieldColors(),
                    singleLine = true,
                    placeholder = { Text("jean.dupont@gmail.com") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mot de passe
            LabeledField(label = "Mot de passe") {
                OutlinedTextField(
                    value = viewModel.password,
                    onValueChange = { viewModel.password = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = LittleRoundedCornerShape,
                    colors = fieldColors(),
                    singleLine = true,
                    placeholder = { Text("fitnesspro123") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            //TODO feature same or not
            // Confirmer le Mot de passe
            LabeledField(label = "Confirmer le mot de passe") {
                OutlinedTextField(
                    value = viewModel.password,
                    onValueChange = { viewModel.password = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = LittleRoundedCornerShape,
                    colors = fieldColors(),
                    singleLine = true,
                    placeholder = { Text("répète ton mot de passe") }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            //TODO Sauvegarde + validate
            // S'inscrire
            Button(
                onClick = onAccountCreationClick,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = LittleRoundedCornerShape,
                colors = ButtonDefaults.buttonColors(containerColor = GreenAccent)
            ) {
                Text(
                    text = "Créer mon compte",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    HealthyBetTheme {
        SignUpScreen()
    }
}

@Composable
private fun LabeledField(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = GrayLabel, style = MaterialTheme.typography.labelMedium);
        Spacer(modifier = Modifier.height(6.dp))
        content()
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = FieldBg,
    unfocusedContainerColor = FieldBg,
    disabledContainerColor = FieldBg,
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = GreenAccent
)
