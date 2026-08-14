package com.example.progettowoc.ui.navigation


import android.annotation.SuppressLint
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.progettowoc.home.HomeScreen
import com.example.progettowoc.notifications.screens.NotificationScreen
import com.example.progettowoc.programs.screens.cliente.ProgramsScreen
import com.example.progettowoc.users.screens.SettingsScreen
import com.example.progettowoc.auth.screens.LoginScreen
import com.example.progettowoc.auth.screens.RegisterScreen
import com.example.progettowoc.coaching.viewmodels.ClienteCoachingRelationViewModel
import com.example.progettowoc.coaching.screens.coach.ClienteRequestScreen
import com.example.progettowoc.coaching.screens.cliente.CoachingRequestScreen
import com.example.progettowoc.coaching.screens.cliente.OwnCoachRelationScreen
import com.example.progettowoc.coaching.screens.coach.RequestsListScreen
import com.example.progettowoc.users.screens.SearchCoachScreen
import com.example.progettowoc.coaching.viewmodels.CoachCoachingRequestViewModel
import com.example.progettowoc.notifications.NotificationViewModel
import com.example.progettowoc.programs.viewmodels.ClienteProgramViewModel
import com.example.progettowoc.programs.screens.cliente.DayScreen
import com.example.progettowoc.programs.screens.cliente.ExerciseScreen
import com.example.progettowoc.programs.screens.coach.EditProgramScreen
import com.example.progettowoc.programs.screens.coach.ClienteInfoEProgramsScreen
import com.example.progettowoc.coaching.screens.coach.CoachClientsListScreen
import com.example.progettowoc.programs.data.ProgramSheet
import com.example.progettowoc.programs.viewmodels.CoachProgramViewModel
import com.example.progettowoc.ui.navigation.CustomNavType.ProgramSheetNavType
import com.example.progettowoc.ui.navigation.CustomNavType.UserNavType
import com.example.progettowoc.users.data.User
import com.example.progettowoc.users.screens.UserScreen
import kotlin.reflect.typeOf


@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun AppNavHost(
    navController: NavHostController,
    notificationViewModel: NotificationViewModel,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = MainRoutes,
        modifier = modifier
    ) {

        navigation<AuthRoutes>(startDestination = AuthRoutes.LoginRoute) {
            composable<AuthRoutes.LoginRoute>(
                enterTransition = { scaleIn() },
                exitTransition = { scaleOut() }
            ) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigateToHome()
                    },
                    onBackClick = { navController.navigateUp() }
                )
            }

            composable<AuthRoutes.RegisterRoute>(
                enterTransition = { scaleIn() },
                exitTransition = { scaleOut() }
            ) {
                RegisterScreen(
                    onBackClick = { navController.navigateUp() },
                    onRegisterSuccess = {
                        navController.navigateToHome()
                    }
                )
            }
        }


        navigation<ProgramRoutes>(startDestination = ProgramRoutes.ProgramsRoute) {
            composable<ProgramRoutes.ProgramsRoute> {
                ProgramsScreen(
                    onDayClick = { navController.navigate(ProgramRoutes.ProgramDayRoute) }
                )
            }

            composable<ProgramRoutes.ProgramDayRoute>(
                enterTransition = { slideInHorizontally { it } },
                exitTransition = { slideOutHorizontally { it } },
                popEnterTransition = { slideInHorizontally { it } },
                popExitTransition = { slideOutHorizontally { it } }
            ) {
                val vm: ClienteProgramViewModel = hiltViewModel(remember { navController.getBackStackEntry<ProgramRoutes.ProgramsRoute>() })
                DayScreen(
                    clienteProgramViewModel = vm,
                    onExerciseClick = { exerciseIndex ->
                        navController.navigate(ProgramRoutes.ProgramExerciseRoute(
                            exerciseIndex = exerciseIndex
                        ))
                    }
                )
            }

            composable<ProgramRoutes.ProgramExerciseRoute>(
                enterTransition = { scaleIn() },
                exitTransition = { scaleOut() }
            ) { backStackEntry ->
                val args = backStackEntry.toRoute<ProgramRoutes.ProgramExerciseRoute>()
                val vm: ClienteProgramViewModel =
                    hiltViewModel(remember { navController.getBackStackEntry<ProgramRoutes.ProgramsRoute>() })
                val uiState = vm.currentProgramUiState.collectAsState()
                val exercise = vm.getExercise(
                    weekNumber = uiState.value.currentWeekNumber ?: return@composable,
                    dayNumber = uiState.value.currentDayNumber ?: return@composable,
                    args.exerciseIndex
                ) ?: return@composable
                ExerciseScreen(
                    exercise = exercise,
                    exerciseIndex = args.exerciseIndex,
                    clienteProgramViewModel = vm
                )
            }
        }


        navigation<UserRoutes>(startDestination = UserRoutes.UserRoute) {
            composable<UserRoutes.UserRoute> {
                UserScreen(
                    onSettingsClick = { navController.navigate(UserRoutes.SettingsRoute) },
                    onSearchCoachClick = { navController.navigate(CoachingRequestRoutes.SearchCoachRoute) },
                    onClienteRequestsClick = { navController.navigate(CoachingRequestRoutes.RequestsListRoute) },
                    onLogoutSuccess = {
                        navController.navigateToHome()
                    },
                    onOwnCoachClick = {
                        navController.navigate(CoachingRelationRoutes.OwnCoachRelationRoute)
                    }
                )
            }

            composable<UserRoutes.SettingsRoute>(
                enterTransition = { scaleIn() },
                exitTransition = { scaleOut() },
                popEnterTransition = { scaleIn() },
                popExitTransition = { scaleOut() }
            ) { SettingsScreen() }
        }


        navigation<MainRoutes>(startDestination = MainRoutes.HomeRoute) {
            composable<MainRoutes.HomeRoute> {
                LaunchedEffect(Unit) {
                    navController.popBackStack(MainRoutes.HomeRoute, inclusive = false)
                }
                HomeScreen(
                    onLoginClick = { navController.navigate(AuthRoutes.LoginRoute) },
                    onRegistratiClick = { navController.navigate(AuthRoutes.RegisterRoute) },
                    onNextWorkOutClick = {
                        navController.navigate(ProgramRoutes.ProgramsRoute)
                        navController.navigate(ProgramRoutes.ProgramDayRoute)
                    }
                )
            }

            composable<MainRoutes.NotificationsRoute>(
                enterTransition = { slideInHorizontally { it } },
                exitTransition = { slideOutHorizontally { it } },
                popEnterTransition = { slideInHorizontally { it } },
                popExitTransition = { slideOutHorizontally { it } }
            ) {
                NotificationScreen(
                    notificationViewModel = notificationViewModel,
                    onRequestsClick = { navController.navigate(CoachingRequestRoutes.RequestsListRoute) },
                    onProgramClick = { navController.navigateToPrograms() },
                    onRequestResultClick = { navController.navigateToUser() }
                )
            }
        }


        navigation<CoachingRequestRoutes>(startDestination = CoachingRequestRoutes.SearchCoachRoute) {
            composable<CoachingRequestRoutes.SearchCoachRoute>(
                enterTransition = { scaleIn() },
                exitTransition = { scaleOut() },
                popEnterTransition = { scaleIn() },
                popExitTransition = { scaleOut() }
            ) {
                SearchCoachScreen(
                    onSelectedCoach = { coach ->
                        navController.navigate(CoachingRequestRoutes.CoachingRequestRoute(coach = coach))
                    }
                )
            }

            composable<CoachingRequestRoutes.CoachingRequestRoute>(
                typeMap = mapOf(typeOf<User>() to UserNavType),
                enterTransition = { slideInHorizontally { it } },
                exitTransition = { slideOutHorizontally { it } },
                popEnterTransition = { slideInHorizontally { it } },
                popExitTransition = { slideOutHorizontally {it} }
            ) { backStackEntry ->
                val args = backStackEntry.toRoute<CoachingRequestRoutes.CoachingRequestRoute>()

                CoachingRequestScreen(
                    coach = args.coach,
                    onInviaClick = { navController.popBackStack(route = UserRoutes.UserRoute, inclusive = false) }
                )
            }

            composable<CoachingRequestRoutes.ClienteRequestRoute>(
                typeMap = mapOf(typeOf<User>() to UserNavType),
                enterTransition = { slideInHorizontally { it } },
                exitTransition = { slideOutHorizontally { it } }
            ) { backStackEntry ->
                val args = backStackEntry.toRoute<CoachingRequestRoutes.ClienteRequestRoute>()

                val vm: CoachCoachingRequestViewModel =
                    hiltViewModel(remember { navController.getBackStackEntry<CoachingRequestRoutes.RequestsListRoute>() })

                ClienteRequestScreen(
                    coachingRequestViewModel = vm,
                    cliente = args.cliente,
                    onButtonClick = { navController.popBackStack() }
                )
            }

            composable<CoachingRequestRoutes.RequestsListRoute>(
                enterTransition = { scaleIn() },
                exitTransition = { scaleOut() },
                popEnterTransition = { scaleIn() },
                popExitTransition = { scaleOut() }
            ) {
                RequestsListScreen(
                    onSelectedUser = { user ->
                        navController.navigate(CoachingRequestRoutes.ClienteRequestRoute(cliente = user))
                    }
                )
            }
        }


        navigation<CoachingRelationRoutes>(startDestination = CoachingRelationRoutes.CoachClientiListRoute) {
            composable<CoachingRelationRoutes.CoachClientiListRoute>(
                popEnterTransition = { slideInHorizontally { -it } }
            ) {
                CoachClientsListScreen(
                    onClienteClick = { cliente ->
                        navController.navigate(
                            CoachingRelationRoutes.CoachCLienteInfoRoute(cliente = cliente)
                        )
                    }
                )
            }

            composable<CoachingRelationRoutes.CoachCLienteInfoRoute>(
                typeMap = mapOf(typeOf<User>() to UserNavType),
                enterTransition = { slideInHorizontally { it } },
                popEnterTransition = { slideInHorizontally { -it } },
            ) { backStackEntry ->
                val args = backStackEntry.toRoute<CoachingRelationRoutes.CoachCLienteInfoRoute>()

                ClienteInfoEProgramsScreen(
                    cliente = args.cliente,
                    onProgramClick = { program ->
                        navController.navigate(
                            CoachingRelationRoutes.EditProgramRoute(
                                clienteId = args.cliente.id,
                                program = program
                            )
                        )
                    },
                    onNewProgramClick = {
                        navController.navigate(
                            CoachingRelationRoutes.EditProgramRoute(
                                clienteId = args.cliente.id,
                                program = null
                            )
                        )
                    }
                )
            }

            composable<CoachingRelationRoutes.EditProgramRoute>(
                typeMap = mapOf(typeOf<ProgramSheet?>() to ProgramSheetNavType),
                enterTransition = { slideInHorizontally { it } },
                exitTransition = { slideOutHorizontally { it } },
                popExitTransition = { slideOutHorizontally { it } }
            ) { backStackEntry ->
                val args = backStackEntry.toRoute<CoachingRelationRoutes.EditProgramRoute>()
                val vm: CoachProgramViewModel =
                    hiltViewModel(remember { navController.getBackStackEntry<CoachingRelationRoutes.CoachCLienteInfoRoute>() })

                EditProgramScreen(
                    coachProgramViewModel = vm,
                    clienteId = args.clienteId,
                    program = args.program,
                    onSaveSuccess = { navController.popBackStack() },
                )
            }

            composable<CoachingRelationRoutes.OwnCoachRelationRoute>(
                enterTransition = { scaleIn() },
                exitTransition = { scaleOut() },
                popEnterTransition = { scaleIn() },
                popExitTransition = { scaleOut() }
            ) {
                val vm: ClienteCoachingRelationViewModel =
                    hiltViewModel(remember { navController.getBackStackEntry<UserRoutes.UserRoute>() })

                OwnCoachRelationScreen(
                    coachingRelationViewModel = vm,
                    onRemoveRelationClick = { navController.navigateToUser() }
                )
            }
        }
    }
}


// per la bottom bar, pulisce il grafo
fun NavController.navigateToHome() {
    navigate(MainRoutes.HomeRoute) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavController.navigateToPrograms() {
    navigate(ProgramRoutes.ProgramsRoute) {
        popUpTo(MainRoutes.HomeRoute) { inclusive = false }
        launchSingleTop = true
    }
}

fun NavController.navigateToUser() {
    navigate(UserRoutes.UserRoute) {
        popUpTo(MainRoutes.HomeRoute) { inclusive = false }
        launchSingleTop = true
    }
}

fun NavController.navigateToClientiList() {
    navigate(CoachingRelationRoutes.CoachClientiListRoute) {
        popUpTo(MainRoutes.HomeRoute) { inclusive = false }
        launchSingleTop = true
    }
}