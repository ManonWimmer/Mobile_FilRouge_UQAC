package com.example.healthybet.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ConnexionViewModel() : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")

}