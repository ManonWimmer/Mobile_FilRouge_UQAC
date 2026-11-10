package com.grman.healthybet

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.grman.healthybet.screens.alarms.AlarmsScreen
import com.grman.healthybet.screens.connexion.ConnexionScreen
import com.grman.healthybet.screens.alarms.CreateAlarmScreen
import com.grman.healthybet.screens.groups.CreateGroupScreen
import com.grman.healthybet.screens.groups.GroupsScreen
import com.grman.healthybet.viewmodels.GroupsViewModel
import com.grman.healthybet.screens.home.HomeScreen
import com.grman.healthybet.screens.connexion.SignUpScreen
import com.grman.healthybet.screens.connexion.WelcomeScreen
import com.grman.healthybet.screens.groups.GroupDetailScreen
import com.grman.healthybet.screens.profile.ProfileScreen

object Routes {
    const val WELCOME = "welcome"
    const val SIGNUP = "signup"
    const val LOGIN = "login"
    const val HOME = "home"
    const val CREATE_ALARM = "create_alarm"
    const val ALARM = "alarm"
    const val GROUPS = "groups"
    const val CREATE_GROUP = "create_group"
    const val GROUP_DETAIL = "group_detail"
    const val PROFILE = "profile"
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
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(350)) + fadeIn(tween(350))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(350), targetOffset = { it / 4 }) + fadeOut(tween(350))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(350), initialOffset = { it / 4 }) + fadeIn(tween(350))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(350)) + fadeOut(tween(350))
        }
    ) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onSignUpClick = { navController.navigate(Routes.SIGNUP) },
                onLogInClick = { navController.navigate(Routes.LOGIN) }
            )
        }

        composable(Routes.SIGNUP) {
            SignUpScreen(
                onAccountCreationClick = {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.WELCOME) { inclusive = true } }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.LOGIN) {
            ConnexionScreen(
                onConnexionClick = {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.WELCOME) { inclusive = true } }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.HOME,
            enterTransition = { fadeIn(tween(250)) },
            exitTransition = { fadeOut(tween(250)) }) {
            HomeScreen(
                onCreateAlarmClick = { navController.navigate(Routes.CREATE_ALARM)},
                onSelectAlarmScreen = { navController.navigate(Routes.ALARM)}
            )
        }

        composable(Routes.ALARM) {
            AlarmsScreen(
                groupsViewModel = groupsViewModel,
                onAlarmClick = { groupId -> navController.navigate("${Routes.GROUP_DETAIL}/$groupId") }
            )
        }

        composable(
            "${Routes.CREATE_ALARM}?groupId={groupId}",
            arguments = listOf(navArgument("groupId") { defaultValue = "" }),
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(400))
            },
            exitTransition = { fadeOut(tween(200)) },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(400))
            }
        ) { entry ->
            CreateAlarmScreen(
                groupsViewModel = groupsViewModel,
                initialGroupId = entry.arguments?.getString("groupId").orEmpty(),
                onClose = { navController.popBackStack() }
            )
        }

        composable(Routes.GROUPS) {
            GroupsScreen(
                viewModel = groupsViewModel,
                onCreateGroupClick = { navController.navigate(Routes.CREATE_GROUP)  },
                onGroupClick = { id -> navController.navigate("${Routes.GROUP_DETAIL}/$id") })
        }

        composable(Routes.CREATE_GROUP) {
            CreateGroupScreen(
                viewModel = groupsViewModel,
                onClose = { navController.popBackStack() },
                onGroupCreated = { navController.popBackStack() }
            )
        }

        composable("${Routes.GROUP_DETAIL}/{groupId}") { entry ->
            GroupDetailScreen(
                groupId = entry.arguments?.getString("groupId") ?: "",
                viewModel = groupsViewModel,
                onBackClick = { navController.popBackStack() },
                onCreateAlarmClick = { id -> navController.navigate("${Routes.CREATE_ALARM}?groupId=$id") }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(0) // vide l'historique pour que "retour" ne ramène pas dans l'app
                    }
                }
            )
        }
    }
}