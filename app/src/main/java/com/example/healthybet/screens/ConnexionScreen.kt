package com.example.healthybet.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthybet.R
import com.example.healthybet.ui.theme.FieldBg
import com.example.healthybet.ui.theme.GrayLabel
import com.example.healthybet.ui.theme.GraySubtitle
import com.example.healthybet.ui.theme.HealthyBetTheme
import com.example.healthybet.ui.theme.GreenAccent

@Composable
fun ConnexionScreen(modifier: Modifier = Modifier) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }


    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {

            // Logo
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(GreenAccent, RoundedCornerShape(18.dp)), // todo: use shape
                contentAlignment = Alignment.Center
            ) {
                Image(painterResource(
                    id=R.drawable.cat),
                    contentDescription = "cat", // todo: change
                    modifier = Modifier.size(20.dp)
                );
            }

            Spacer(modifier = Modifier.height(12.dp))

            // App name
            Text(
                text = "Healthy Bet",
                color = Color.White, // todo: use color
                fontSize = 20.sp, // todo : use type
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Text 1
            Text(
                text = "Ravis de te revoir !",
                color = Color.White, // todo: use color
                fontSize = 26.sp, // todo: use type
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Text 2
            Text(
                text = "Connecte-toi pour ne pas rater ton prochain pari",
                color = GraySubtitle,
                fontSize = 14.sp, // todo: use type
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Email
            LabeledField(label = "Adresse Email") {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp), // todo: use shape
                    colors = fieldColors(),
                    singleLine = true,
                    placeholder = { Text("jean.dupont@gmail.com") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mot de passe
            LabeledField(label = "Mot de passe") {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp), // todo: use shape
                    colors = fieldColors(),
                    singleLine = true,
                    placeholder = { Text("fitnesspro123") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mot de passe oublié
            Text( // todo: on click
                text = "Mot de passe oublié ?",
                color = GreenAccent,
                fontSize = 13.sp, //todo: use type
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 4.dp),
                textAlign = TextAlign.End
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Se connecter
            Button(
                onClick = { /* todo */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp), // todo: use shape
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenAccent
                )
            ) {
                Text(
                    text = "Se connecter",
                    color = Color(0xFF0B0E16), // todo: use color
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp // todo : use type
                )
            }


        }
    }



}

@Preview(showBackground = true)
@Composable
fun ConnexionScreenPreview() {
    HealthyBetTheme {
        ConnexionScreen()
    }
}

@Composable
private fun LabeledField(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = GrayLabel, fontSize = 13.sp) // todo: use type
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
