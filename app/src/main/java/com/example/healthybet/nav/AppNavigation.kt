package com.grman.healthybet

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.grman.healthybet.screens.AlarmsScreen
import com.grman.healthybet.screens.ConnexionScreen
import com.grman.healthybet.screens.CreateAlarmScreen
import com.grman.healthybet.screens.CreateGroupScreen
import com.grman.healthybet.screens.GroupsScreen
import com.grman.healthybet.screens.GroupsViewModel
import com.grman.healthybet.screens.HomeScreen
import com.grman.healthybet.screens.SignUpScreen
import com.grman.healthybet.screens.WelcomeScreen

object Routes {
    const val WELCOME = "welcome"
    const val SIGNUP = "signup"
    const val LOGIN = "login"
    const val HOME = "home"
    const val CREATE_ALARM = "create_alarm"
    const val ALARM = "alarm"
    const val GROUPS = "groups"
    const val CREATE_GROUP = "create_group"
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier,
                  navController: NavHostController, ) {
    //val navController = rememberNavController()

    // temp avant firebase
    val groupsViewModel: GroupsViewModel = viewModel() // Parce que sinon ça recrée une instance dans groups et create groups et c pas la meme

    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME,
        modifier = modifier
    ) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onSignUpClick = { navController.navigate(Routes.SIGNUP) },
                onLogInClick = { navController.navigate(Routes.LOGIN) }
            )
        }

        composable(Routes.SIGNUP) {
            SignUpScreen(
                onAccountCreationClick = { navController.navigate(Routes.HOME)}
            )
        }

        composable(Routes.LOGIN) {
            ConnexionScreen(
                onConnexionClick = { navController.navigate(Routes.HOME)}
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onCreateAlarmClick = { navController.navigate(Routes.CREATE_ALARM)},
                onSelectAlarmScreen = { navController.navigate(Routes.ALARM)}
            )
        }

        composable(Routes.CREATE_ALARM){
            CreateAlarmScreen()
        }

        composable (Routes.ALARM) {
            AlarmsScreen()
        }

        composable(Routes.GROUPS) {
            GroupsScreen(viewModel = groupsViewModel, onCreateGroupClick = { navController.navigate(Routes.CREATE_GROUP)  })
        }

        composable(Routes.CREATE_GROUP) {
            CreateGroupScreen(
                viewModel = groupsViewModel,
                onClose = { navController.popBackStack() },
                onGroupCreated = { navController.popBackStack() }
            )
        }
    }
}