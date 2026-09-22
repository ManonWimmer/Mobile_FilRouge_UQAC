package com.example.healthybet.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.healthybet.R
import com.example.healthybet.ui.theme.HealthyBetTheme

@Composable
fun ConnexionScreen(name: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {

        val textModifier = Modifier.padding(PaddingValues(30.dp));

        Text(text = name, color= Color.White, modifier=textModifier, textAlign = TextAlign.Center);

        val imageModifier = Modifier.size(150.dp).padding(PaddingValues(10.dp));

        Image(painterResource(
            id=R.drawable.cat),
            contentDescription = "cat",
            modifier = imageModifier
        );

        val textFieldModifier = Modifier.padding(PaddingValues( 5.dp))

        TextField(
            state = rememberTextFieldState(),
            placeholder = { Text("Enter Username") },
            modifier = textFieldModifier
        )
        TextField(
            state = rememberTextFieldState(),
            placeholder = { Text("Enter Password") },
            modifier = textFieldModifier
        )
    }

}

@Preview(showBackground = true)
@Composable
fun ConnexionScreenPreview() {
    HealthyBetTheme {
        ConnexionScreen("Healthy Bet")
    }
}