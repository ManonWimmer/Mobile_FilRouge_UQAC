package com.grman.healthybet.screens.settings

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grman.healthybet.ui.theme.HealthyBetTheme
import com.grman.healthybet.viewmodels.ConnexionViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore("user_prefs")

@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {

    val viewModel : ConnexionViewModel = viewModel()

    val notificationsKey = booleanPreferencesKey("notificationsEnabled")
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = context.dataStore
    val savedNotificationsEnabled = dataStore.data.map { preferences ->
        preferences[notificationsKey] ?: true
    }.collectAsState(initial = true)

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Column(modifier = Modifier.widthIn(min = 400.dp, max = 600.dp).align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {

            Switch(
                checked = savedNotificationsEnabled.value as Boolean,
                onCheckedChange = {
                    scope.launch {
                        dataStore.edit { preferences ->
                            preferences[notificationsKey] = it
                        }
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    HealthyBetTheme {
        SettingsScreen()
    }
}
