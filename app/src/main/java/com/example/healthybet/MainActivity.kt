package com.example.healthybet

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
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
import com.example.healthybet.screens.ConnexionScreen
import com.example.healthybet.ui.theme.HealthyBetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate")
        enableEdgeToEdge()
        setContent {
            HealthyBetTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ConnexionScreen(
                        name = "Healthy Bet",
                        modifier = Modifier.padding(innerPadding).fillMaxWidth().background(Color.DarkGray)
                    )
                }
            }
        }
    }
    override fun onStart()
    {
        super.onStart()
        Log.d("MainActivity", "onStart")
    }
    override fun onResume()
    {
        super.onResume()
        Log.d("MainActivity", "onResume")
    }
    override fun onPause()
    {
        super.onPause()
        Log.d("MainActivity", "onPause")
    }
    override fun onStop()
    {
        super.onStop()
        Log.d("MainActivity", "onStop")
    }
    override fun onDestroy()
    {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy")
    }
    override fun onRestart()
    {
        super.onRestart()
        Log.d("MainActivity", "onRestart")
    }
}

