package com.grman.healthybet.screens.connexion

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grman.healthybet.R
import com.grman.healthybet.data.auth.AuthRepository
import com.grman.healthybet.items.common.AppButton
import com.grman.healthybet.items.AuthLayout
import com.grman.healthybet.items.AuthTitle
import com.grman.healthybet.items.ErrorMessage
import com.grman.healthybet.items.common.LabeledTextField
import com.grman.healthybet.items.LogoHeader
import com.grman.healthybet.items.common.AppButton
import com.grman.healthybet.items.common.LabeledTextField
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.HealthyBetTheme
import com.grman.healthybet.viewmodels.ConnexionViewModel

@Composable
fun ConnexionScreen(
    modifier: Modifier = Modifier,
    onConnexionClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    viewModel: ConnexionViewModel = viewModel()
) {
    AuthLayout(modifier = modifier, onBackClick = onBackClick) {
        LogoHeader()

        Spacer(modifier = Modifier.height(28.dp))

        AuthTitle(
            title = stringResource(R.string.connexion_greeting),
            subtitle = stringResource(R.string.app_pitch_connexion)
        )

        Spacer(modifier = Modifier.height(28.dp))

        LabeledTextField(
            label = stringResource(R.string.e_mail),
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            placeholder = stringResource(R.string.e_mail_example)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LabeledTextField(
            label = stringResource(R.string.password),
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            placeholder = stringResource(R.string.password_example),
            isPassword = true
        )

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

        ErrorMessage(viewModel.errorMessage)

        AppButton(
            text = stringResource(R.string.connect),
            onClick = { viewModel.signIn(onSuccess = onConnexionClick) },
            enabled = !viewModel.isLoading,
            loading = viewModel.isLoading,
            height = 52.dp
        )
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