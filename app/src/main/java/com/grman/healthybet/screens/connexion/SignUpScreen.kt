    package com.grman.healthybet.screens.connexion

    import androidx.compose.animation.AnimatedVisibility
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.background
    import androidx.compose.foundation.border
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.FlowRow
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.imePadding
    import androidx.compose.foundation.layout.navigationBarsPadding
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.size
    import androidx.compose.foundation.layout.statusBarsPadding
    import androidx.compose.foundation.layout.widthIn
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.text.KeyboardOptions
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material3.Button
    import androidx.compose.material3.ButtonDefaults
    import androidx.compose.material3.CircularProgressIndicator
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.OutlinedTextField
    import androidx.compose.material3.OutlinedTextFieldDefaults
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
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
    import com.grman.healthybet.viewmodels.SignUpViewModel

    private val memberEmojis = listOf("\uD83D\uDE00", "\uD83D\uDE01️", "☺\uFE0F", "\uD83D\uDE07", "\uD83E\uDD70", "\uD83E\uDD2A", "\uD83E\uDD78", "\uD83E\uDEE1", "\uD83D\uDE0E", "\uD83D\uDE0B") // emojis à choisiri

    @Composable
    fun SignUpScreen(modifier: Modifier = Modifier,
                     onAccountCreationClick: () -> Unit = {},
                     onBackClick: () -> Unit = {},
                     viewModel: SignUpViewModel = viewModel()) {

        Box(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp)
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 72.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Logo
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(GreenAccent, MediumRoundedCornerShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(painterResource(
                        id=R.drawable.logo),
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
                    text = "Crée ton compte",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Text 2
                Text(
                    text = stringResource(R.string.app_pitch_sign_up),
                    color = GraySubtitle,
                    fontSize = 14.sp, // todo: use type
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Emojis (comme dans create group)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    memberEmojis.forEach { item ->
                        val selected = item == viewModel.emoji
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(if (selected) GreenAccent.copy(alpha = 0.15f) else FieldBg, MediumRoundedCornerShape)
                                .border(
                                    1.dp,
                                    if (selected) GreenAccent else Color.White.copy(alpha = 0.1f),
                                    MediumRoundedCornerShape
                                )
                                .clickable { viewModel.emoji = item },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = item, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pseudo
                LabeledField(label = stringResource(R.string.pseudo)) {
                    OutlinedTextField(
                        value = viewModel.pseudo,
                        onValueChange = { viewModel.pseudo = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = LittleRoundedCornerShape,
                        colors = fieldColors(),
                        singleLine = true,
                        placeholder = { Text(stringResource(R.string.pseudo_example)) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // EMail
                LabeledField(label = stringResource(R.string.e_mail)) {
                    OutlinedTextField(
                        value = viewModel.email,
                        onValueChange = { viewModel.email = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = LittleRoundedCornerShape,
                        colors = fieldColors(),
                        singleLine = true,
                        placeholder = { Text(stringResource(R.string.e_mail_example)) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mot de passe
                LabeledField(label = stringResource(R.string.password)) {
                    OutlinedTextField(
                        value = viewModel.password,
                        onValueChange = { viewModel.password = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = LittleRoundedCornerShape,
                        colors = fieldColors(),
                        singleLine = true,
                        placeholder = { Text(stringResource(R.string.password_example)) },
                       visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LabeledField(label = stringResource(R.string.password_confirm)) {
                    OutlinedTextField(
                        value = viewModel.confirmPassword,
                        onValueChange = { viewModel.confirmPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = LittleRoundedCornerShape,
                        colors = fieldColors(),
                        singleLine = true,
                        placeholder = { Text(stringResource(R.string.password_confirm_example)) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Message d'erreur sign up
                AnimatedVisibility(visible = viewModel.errorMessage != null) {
                    Column {
                        Text(text = viewModel.errorMessage.orEmpty(), color = Color(0xFFFF6B6B), fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // S'inscrire
                Button(
                    onClick = { viewModel.signUp(onSuccess = onAccountCreationClick) },
                    enabled = !viewModel.isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = LittleRoundedCornerShape,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenAccent)
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(stringResource(R.string.create_account), style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            // Bouton retour
            BackButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.TopStart).padding(top = 16.dp)
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
