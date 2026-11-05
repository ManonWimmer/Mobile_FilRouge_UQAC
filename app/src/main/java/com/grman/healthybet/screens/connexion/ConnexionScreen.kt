package com.grman.healthybet.screens.connexion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grman.healthybet.R
import com.grman.healthybet.data.auth.AuthRepository
import com.grman.healthybet.items.BackButton
import com.grman.healthybet.ui.theme.FieldBg
import com.grman.healthybet.ui.theme.GrayLabel
import com.grman.healthybet.ui.theme.GraySubtitle
import com.grman.healthybet.ui.theme.HealthyBetTheme
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.LittleRoundedCornerShape
import com.grman.healthybet.ui.theme.MediumRoundedCornerShape
import com.grman.healthybet.viewmodels.ConnexionViewModel

@Composable
fun ConnexionScreen(modifier: Modifier = Modifier,
                    onConnexionClick : () -> Unit = {},
                    onBackClick: () -> Unit = {},
                    viewModel: ConnexionViewModel = viewModel()) {

    var passwordVisible by remember { mutableStateOf(false) }


    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 24.dp)
    ) {

        // Bouton retour
        BackButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.TopStart).padding(top = 16.dp)
        )

        Column(modifier = Modifier.widthIn(min = 400.dp, max = 600.dp).align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {

            // Logo
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(GreenAccent, MediumRoundedCornerShape),
                contentAlignment = Alignment.Center
            ) {
                Image(painterResource(
                    id= R.drawable.logo),
                    contentDescription = stringResource(R.string.logo_app_description),
                    modifier = Modifier.size(40.dp)
                );
            }

            Spacer(modifier = Modifier.height(12.dp))

            // App name
            Text(
                text = stringResource(R.string.app_name),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Text 1
            Text(
                text = stringResource(R.string.connexion_greeting),
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Text 2
            Text(
                text = stringResource(R.string.app_pitch_connexion),
                color = GraySubtitle,
                fontSize = 14.sp, // todo: use type
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Email
            LabeledField(label = stringResource(R.string.e_mail),) {
                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = LittleRoundedCornerShape,
                    colors = fieldColors(),
                    singleLine = true,
                    placeholder = { Text(stringResource(R.string.e_mail_example),) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mot de passe
            LabeledField(label = stringResource(R.string.password),) {
                OutlinedTextField(
                    value = viewModel.password,
                    onValueChange = { viewModel.password = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = LittleRoundedCornerShape,
                    colors = fieldColors(),
                    singleLine = true,
                    placeholder = { Text(stringResource(R.string.password_example),) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mot de passe oublié
            Text( // todo: on click
                text = stringResource(R.string.forgot_password),
                color = GreenAccent,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.fillMaxWidth().padding(end = 4.dp),
                textAlign = TextAlign.End
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Msg d'erreur
            AnimatedVisibility(visible = viewModel.errorMessage != null) {
                Column {
                    Text(text = viewModel.errorMessage.orEmpty(), color = Color(0xFFFF6B6B), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Se connecter
            Button(
                onClick = { viewModel.signIn(onSuccess = onConnexionClick) },
                enabled = !viewModel.isLoading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = LittleRoundedCornerShape,
                colors = ButtonDefaults.buttonColors(containerColor = GreenAccent)
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.connect), style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
fun ConnexionScreenPreview() {
    val fakeRepo = object : AuthRepository {
        override suspend fun signIn(email: String, password: String) {}
        override suspend fun sendPasswordReset(email: String) {}
        override suspend fun signUp(email: String, password: String, pseudo: String, emoji: String) {}
    }

    HealthyBetTheme {
        ConnexionScreen(viewModel = ConnexionViewModel(fakeRepo))
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
