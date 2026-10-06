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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.healthybet.items.BottomNavBar
import com.example.healthybet.items.BottomNavItem
import com.example.healthybet.screens.AlarmsScreen
import com.example.healthybet.screens.ConnexionScreen
import com.example.healthybet.screens.CreateAlarmScreen
import com.example.healthybet.screens.HomeScreen
import com.example.healthybet.screens.SettingsScreen
import com.example.healthybet.screens.WelcomeScreen
import com.example.healthybet.ui.theme.HealthyBetTheme
import com.example.healthybet.ui.theme.BgColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate")
        enableEdgeToEdge()
        setContent {
            HealthyBetTheme {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route

                // Affichge pas la bottom barre
                val bottomBarNotRoutes = listOf(Routes.WELCOME, Routes.SIGNUP, Routes.LOGIN)
                val dontShowBottomBar = currentRoute in bottomBarNotRoutes


                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = BgColor,
                    bottomBar = {
                        //Scaffold - Bottom barre conditionnelle
                        if (!dontShowBottomBar) {
                            BottomNavBar(

                                // Change le logo selon la fenetre so far pas besoin de plus
                                selected = when (currentRoute) {
                                    Routes.ALARM -> BottomNavItem.Alarms
                                    else -> BottomNavItem.Home
                                },
                                onItemSelected = { item ->
                                    val route = when (item) {
                                        BottomNavItem.Alarms -> Routes.ALARM
                                        else -> Routes.HOME
                                    }
                                    navController.navigate(route) {
                                        popUpTo(Routes.HOME) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    // Test settings data store
                    SettingsScreen(Modifier.padding(innerPadding));

                    /*
                    AppNavigation(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                    */

                }


                /*
                var selectedTab by remember { mutableStateOf(BottomNavItem.Home) }

                Scaffold(modifier = Modifier.fillMaxSize(), containerColor = BgColor, bottomBar = {
                    BottomNavBar(
                        selected = selectedTab,
                        onItemSelected = { selectedTab = it }
                    )
                }) { innerPadding ->

                    /*
                    when(selectedTab)
                    {
                        // Home (tablet tested)
                        BottomNavItem.Home -> HomeScreen(modifier = Modifier.padding(innerPadding).fillMaxSize().background(BgColor))

                        // Alarms
                        BottomNavItem.Alarms -> AlarmsScreen(modifier = Modifier.padding(innerPadding).fillMaxSize().background(BgColor))

                        else -> HomeScreen(modifier = Modifier.padding(innerPadding).fillMaxSize().background(BgColor))
                    }
                    */

                    // Test create alarm
                    //CreateAlarmScreen(modifier = Modifier.padding(innerPadding).fillMaxSize().background(BgColor), {/* to do */});



                    // Connexion (tablet tested)
                    //WelcomeScreen(modifier = Modifier.padding(PaddingValues(0.dp)).fillMaxSize().background(BgColor))
                    AppNavigation(modifier = Modifier.padding(innerPadding))
                }

                 */
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

