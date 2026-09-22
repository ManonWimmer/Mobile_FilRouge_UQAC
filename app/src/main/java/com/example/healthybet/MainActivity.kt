package com.example.healthybet

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.savedstate.SavedState
import com.example.healthybet.ui.theme.HealthyBetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate")
        enableEdgeToEdge()
        setContent {
            HealthyBetTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HealthyBetTheme {
        Greeting("Android")
    }
}