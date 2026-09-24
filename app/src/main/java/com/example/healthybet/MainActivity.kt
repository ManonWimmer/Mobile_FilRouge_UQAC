package com.example.healthybet

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.healthybet.items.BottomNavBar
import com.example.healthybet.items.BottomNavItem
import com.example.healthybet.screens.ConnexionScreen
import com.example.healthybet.ui.theme.HealthyBetTheme
import com.example.healthybet.ui.theme.BgColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate")
        enableEdgeToEdge()
        setContent {
            HealthyBetTheme {
                var selectedTab by remember { mutableStateOf(BottomNavItem.Accueil) }

                Scaffold(modifier = Modifier.fillMaxSize(), containerColor = BgColor, bottomBar = {
                    BottomNavBar(
                        selected = selectedTab,
                        onItemSelected = { selectedTab = it }
                    )
                }) { innerPadding ->

                    // todo: change screen depending on nav bar item selected

                    ConnexionScreen(
                        modifier = Modifier.padding(PaddingValues(0.dp)).fillMaxSize().background(BgColor),
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

