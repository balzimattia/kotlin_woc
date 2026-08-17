package com.example.progettowoc.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import com.example.progettowoc.R
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.progettowoc.NetworkObserver
import com.example.progettowoc.auth.viewmodels.AuthViewModel
import com.example.progettowoc.notifications.NotificationViewModel
import com.example.progettowoc.ui.components.BackArrowButtonComp
import com.example.progettowoc.ui.navigation.AppNavHost
import com.example.progettowoc.ui.navigation.CoachingRelationRoutes
import com.example.progettowoc.ui.navigation.MainRoutes
import com.example.progettowoc.ui.navigation.ProgramRoutes
import com.example.progettowoc.ui.navigation.UserRoutes
import com.example.progettowoc.ui.navigation.navigateToClientiList
import com.example.progettowoc.ui.navigation.navigateToHome
import com.example.progettowoc.ui.navigation.navigateToPrograms
import com.example.progettowoc.ui.navigation.navigateToUser
import com.example.progettowoc.ui.navigation.noScaffoldScreens
import com.example.progettowoc.ui.navigation.onlyTopBar
import com.example.progettowoc.ui.navigation.rootScreens
import com.example.progettowoc.ui.theme.ProgettoWOCTheme
import com.example.progettowoc.users.data.User
import com.example.progettowoc.users.data.UserRole
import io.github.jan.supabase.auth.status.SessionStatus


@Composable
fun AppScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    notificationViewModel: NotificationViewModel = hiltViewModel(),
    networkObserver: NetworkObserver
) {
    val navController = rememberNavController()
    val sessionStatus by authViewModel.sessionStatus.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val notifications by notificationViewModel.notifications.collectAsState()
    val isConnected by networkObserver.isConnected.collectAsState()

    val isLoadingUser = sessionStatus is SessionStatus.Authenticated && currentUser == null

    LaunchedEffect(currentUser?.id) {
        if (currentUser != null) {
            notificationViewModel.loadNotifications()
        } else {
            notificationViewModel.clearNotifications()
        }
    }

    AppContent(
        currentUser = currentUser,
        navController = navController,
        hasNotification = notifications.isNotEmpty(),
        content = { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoadingUser) {
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    AppNavHost(
                        navController = navController,
                        notificationViewModel = notificationViewModel,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }

                if (!isConnected) {
                    NoWiFiContent(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    )
                }
            }
        }
    )
}


@Composable
private fun AppContent(
    currentUser: User?,
    navController: NavHostController,
    hasNotification: Boolean,
    content: @Composable (PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isRoot = currentDestination == null || noScaffoldScreens.none { currentDestination.hasRoute(it) }
    val noBottomoBar = currentDestination == null || onlyTopBar.none { currentDestination.hasRoute(it) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        topBar = {
            if (currentUser != null) {
                if (isRoot) {
                    TopBar(
                        hasNotifications = hasNotification,
                        currentDestination = currentDestination,
                        onNotificationsClick = { navController.navigate(MainRoutes.NotificationsRoute) },
                        showBackArrow = currentDestination != null && rootScreens.none {
                            currentDestination.hasRoute(it)
                        },
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        },

        bottomBar = {
            if (currentUser != null) {
                if (isRoot && noBottomoBar) {
                    BottomBar(
                        currentBackStackEntry = currentDestination,
                        onHomeClick = { navController.navigateToHome() },
                        onProgramsClick = {
                            if (currentUser.role == UserRole.CLIENTE) navController.navigateToPrograms()
                            else if (currentUser.role == UserRole.COACH) navController.navigateToClientiList()
                        },
                        onUserClick = { navController.navigateToUser() }
                    )
                }
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}


@Composable
private fun NoWiFiContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Text(
                "Connessione assente",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    hasNotifications: Boolean,
    currentDestination: NavDestination?,
    onNotificationsClick: () -> Unit,
    showBackArrow: Boolean,
    onBackClick: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
        title = {
            Text(
                "W O C",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
            )
        },
        navigationIcon = {
            if (showBackArrow) {
                BackArrowButtonComp(onBackClick = onBackClick)
            }
        },
        actions = {

            val color =
                if (currentDestination?.hasRoute<MainRoutes.NotificationsRoute>() == true) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface

            BadgedBox(
                badge = {
                    if (hasNotifications) {
                        Badge(containerColor = Color.Red)
                    }
                },
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onNotificationsClick() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.notifications_bell),
                    contentDescription = "Notifiche",
                    modifier = Modifier.size(35.dp),
                    tint = color
                )
            }

        },

        scrollBehavior = scrollBehavior,
    )
}


@Composable
private fun BottomBar(
    currentBackStackEntry: NavDestination?,
    onHomeClick: () -> Unit,
    onProgramsClick: () -> Unit,
    onUserClick: () -> Unit
) {
    NavigationBar(
        windowInsets = NavigationBarDefaults.windowInsets,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
    ) {

        val colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurface,
            indicatorColor = Color.Transparent
        )

        NavigationBarItem(
            selected = currentBackStackEntry?.hasRoute<MainRoutes.HomeRoute>() == true,
            onClick = onHomeClick,
            icon = {
                BottomBarIcon(
                    painter = painterResource(R.drawable.home),
                    contentDescription = "HomeRoute"
                )
            },
            colors = colors
        )

        NavigationBarItem(
            selected = currentBackStackEntry?.hasRoute<ProgramRoutes.ProgramsRoute>() == true || currentBackStackEntry?.hasRoute<CoachingRelationRoutes.CoachClientiListRoute>() == true,
            onClick = onProgramsClick,
            icon = {
                BottomBarIcon(
                    painter = painterResource(R.drawable.programs),
                    contentDescription = "Programmi"
                )
            },
            colors = colors
        )

        NavigationBarItem(
            selected = currentBackStackEntry?.hasRoute<UserRoutes.UserRoute>() == true,
            onClick = onUserClick,
            icon = {
                BottomBarIcon(
                    painter = painterResource(R.drawable.user),
                    contentDescription = "Utente"
                )
            },
            colors = colors
        )
    }
}


@Composable
private fun BottomBarIcon(painter: Painter, contentDescription: String) {
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        modifier = Modifier.size(35.dp)
    )
}


@Preview
@Composable
private fun AppScreenPreview() {
    val navController = rememberNavController()

    ProgettoWOCTheme {
        AppContent(
            hasNotification = true,
            navController = navController,
            currentUser = User("", "", "", UserRole.CLIENTE),
            content = {}
        )
    }
}