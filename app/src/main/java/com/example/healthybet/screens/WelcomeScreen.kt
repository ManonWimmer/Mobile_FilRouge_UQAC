package com.example.healthybet.screens
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.healthybet.R
import com.example.healthybet.ui.theme.FieldBg
import com.example.healthybet.ui.theme.GrayLabel
import com.example.healthybet.ui.theme.HealthyBetTheme
import com.example.healthybet.ui.theme.GreenAccent
import com.example.healthybet.ui.theme.LittleRoundedCornerShape
import com.example.healthybet.ui.theme.MediumRoundedCornerShape

@Composable
fun WelcomeScreen(modifier: Modifier = Modifier,
                  onSignUpClick: () -> Unit = {},
                  onLogInClick: () -> Unit = {}
                  ) {
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
                    contentDescription = "Logo",
                    modifier = Modifier.size(20.dp)
                );
            }

            Spacer(modifier = Modifier.height(10.dp))

            // App Name
            Text(
                text = "Healthy Bet",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Main Title
            Text(
                text = "Un réveil qui motive vraiment.",
                color = Color(0xFF29CE83),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Description
            Text(
                text = "Prends soin de ton corps pour prouver à tes potes qu'ils ont tort de douter de toi. " +
                        "Relève le défi ou paie le gage !",
                color = GrayLabel,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Inscription
            Button(
                onClick = onSignUpClick,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = LittleRoundedCornerShape,
                colors = ButtonDefaults.buttonColors(containerColor = GreenAccent)
            ) {
                Text(
                    text = "Inscription",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Connnexion
            Button(
                onClick = onLogInClick,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = LittleRoundedCornerShape,
                colors = ButtonDefaults.buttonColors(containerColor = FieldBg)
            ) {
                Text(
                    text = "Connexion",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomescreenPreview() {
    HealthyBetTheme {
        WelcomeScreen()
    }
}
