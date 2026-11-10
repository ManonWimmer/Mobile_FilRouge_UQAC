package com.grman.healthybet.screens.connexion

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.grman.healthybet.R
import com.grman.healthybet.items.common.AppButton
import com.grman.healthybet.items.AuthLayout
import com.grman.healthybet.items.LogoHeader
import com.grman.healthybet.items.common.AppButton
import com.grman.healthybet.ui.theme.FieldBg
import com.grman.healthybet.ui.theme.GrayLabel
import com.grman.healthybet.ui.theme.GreenAccent
import com.grman.healthybet.ui.theme.HealthyBetTheme

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onSignUpClick: () -> Unit = {},
    onLogInClick: () -> Unit = {}
) {
    AuthLayout(modifier = modifier) {
        LogoHeader(nameStyle = MaterialTheme.typography.labelLarge)

        Spacer(modifier = Modifier.height(80.dp))

        Text(
            text = stringResource(R.string.app_title),
            color = GreenAccent,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.app_pitch),
            color = GrayLabel,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        AppButton(
            text = stringResource(R.string.sign_up),
            onClick = onSignUpClick,
            height = 52.dp
        )

        Spacer(modifier = Modifier.height(10.dp))

        AppButton(
            text = stringResource(R.string.log_in),
            onClick = onLogInClick,
            height = 52.dp,
            containerColor = FieldBg
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
fun WelcomeScreenPreview() {
    HealthyBetTheme {
        WelcomeScreen()
    }
}