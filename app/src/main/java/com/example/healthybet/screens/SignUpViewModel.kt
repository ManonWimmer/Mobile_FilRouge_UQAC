package com.example.healthybet.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SignUpViewModel : ViewModel() {
    var pseudo by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
}