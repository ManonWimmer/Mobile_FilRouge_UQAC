package com.grman.healthybet.screens.connexion

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grman.healthybet.R
import com.grman.healthybet.data.auth.AuthRepository
import com.grman.healthybet.items.common.AppButton
import com.grman.healthybet.items.AuthLayout
import com.grman.healthybet.items.AuthTitle
import com.grman.healthybet.items.common.EmojiPicker
import com.grman.healthybet.items.ErrorMessage
import com.grman.healthybet.items.common.LabeledTextField
import com.grman.healthybet.items.LogoHeader
import com.grman.healthybet.items.common.AppButton
import com.grman.healthybet.items.common.EmojiPicker
import com.grman.healthybet.items.common.LabeledTextField
import com.grman.healthybet.ui.theme.HealthyBetTheme
import com.grman.healthybet.viewmodels.SignUpViewModel

private val profileEmojis = listOf("\uD83D\uDE00", "\uD83D\uDE01️", "☺\uFE0F", "\uD83D\uDE07", "\uD83E\uDD70", "\uD83E\uDD2A", "\uD83E\uDD78", "\uD83E\uDEE1", "\uD83D\uDE0E", "\uD83D\uDE0B")

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onAccountCreationClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    viewModel: SignUpViewModel = viewModel()
) {
    AuthLayout(modifier = modifier, onBackClick = onBackClick) {
        LogoHeader()

        Spacer(modifier = Modifier.height(28.dp))

        AuthTitle(
            title = stringResource(R.string.sign_up),
            subtitle = stringResource(R.string.app_pitch_sign_up)
        )

        Spacer(modifier = Modifier.height(28.dp))

        EmojiPicker(
            emojis = profileEmojis,
            selected = viewModel.emoji,
            onSelect = { viewModel.emoji = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LabeledTextField(
            label = stringResource(R.string.pseudo),
            value = viewModel.pseudo,
            onValueChange = { viewModel.pseudo = it },
            placeholder = stringResource(R.string.pseudo_example)
        )

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(16.dp))

        LabeledTextField(
            label = stringResource(R.string.password_confirm),
            value = viewModel.confirmPassword,
            onValueChange = { viewModel.confirmPassword = it },
            placeholder = stringResource(R.string.password_confirm_example),
            isPassword = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        ErrorMessage(viewModel.errorMessage)

        AppButton(
            text = stringResource(R.string.create_account),
            onClick = { viewModel.signUp(onSuccess = onAccountCreationClick) },
            enabled = !viewModel.isLoading,
            loading = viewModel.isLoading,
            height = 52.dp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
fun SignUpScreenPreview() {
    val fakeRepo = object : AuthRepository {
        override suspend fun signIn(email: String, password: String) {}
        override suspend fun sendPasswordReset(email: String) {}
        override suspend fun signUp(email: String, password: String, pseudo: String, emoji: String) {}
    }

    HealthyBetTheme {
        SignUpScreen(viewModel = SignUpViewModel(fakeRepo))
    }
}