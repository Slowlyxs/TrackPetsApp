package com.trackpets.app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.trackpets.app.presentation.alerts.*
import com.trackpets.app.presentation.devices.*
import com.trackpets.app.presentation.geofences.*
import com.trackpets.app.presentation.login.*
import com.trackpets.app.presentation.owners.*
import com.trackpets.app.presentation.pets.*
import com.trackpets.app.presentation.users.*
import com.trackpets.app.presentation.dashboard.*

@Composable
fun TrackPetsNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Login & Register
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToMain = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToMain = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.Dashboard.route) {
            WithBottomNav(navController, Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToPets = { navController.navigate(Screen.PetList.route) },
                    onNavigateToDevices = { navController.navigate(Screen.DeviceList.route) },
                    onNavigateToGeofences = { navController.navigate(Screen.GeofenceList.route) },
                    onNavigateToAlerts = { navController.navigate(Screen.AlertList.route) }
                )
            }
        }

        // Pets
        composable(Screen.PetList.route) { backStackEntry ->
            val needsRefresh = backStackEntry.savedStateHandle.get<Boolean>("refresh") == true
            val viewModel: PetViewModel = hiltViewModel()
            LaunchedEffect(needsRefresh) {
                if (needsRefresh) {
                    viewModel.refreshList()
                    backStackEntry.savedStateHandle.remove<Boolean>("refresh")
                }
            }
            WithBottomNav(navController, Screen.PetList.route) {
                PetListScreen(
                    onNavigateToDetail = { id -> navController.navigate(Screen.PetDetail.createRoute(id)) },
                    onNavigateToCreate = { navController.navigate(Screen.PetForm.createRoute()) },
                    viewModel = viewModel
                )
            }
        }
        composable(Screen.PetDetail.route, arguments = listOf(navArgument("id") { type = NavType.IntType })) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            val refresh by backStackEntry.savedStateHandle.getStateFlow("refresh", false).collectAsState()
            PetDetailScreen(
                id = id,
                refreshKey = refresh,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { editId -> navController.navigate(Screen.PetForm.createRoute(editId)) },
                onDeleted = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true)
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.PetForm.route, arguments = listOf(navArgument("id") { type = NavType.StringType; nullable = true })) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            PetFormScreen(
                id = id,
                onNavigateBack = { navController.popBackStack() },
                onSaved = { navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true) }
            )
        }

        // Owners
        composable(Screen.OwnerList.route) { backStackEntry ->
            val needsRefresh = backStackEntry.savedStateHandle.get<Boolean>("refresh") == true
            val viewModel: OwnerViewModel = hiltViewModel()
            LaunchedEffect(needsRefresh) {
                if (needsRefresh) { viewModel.refreshList(); backStackEntry.savedStateHandle.remove<Boolean>("refresh") }
            }
            WithBottomNav(navController, Screen.OwnerList.route) {
                OwnerListScreen(
                    onNavigateToDetail = { id -> navController.navigate(Screen.OwnerDetail.createRoute(id)) },
                    onNavigateToCreate = { navController.navigate(Screen.OwnerForm.createRoute()) },
                    viewModel = viewModel
                )
            }
        }
        composable(Screen.OwnerDetail.route, arguments = listOf(navArgument("id") { type = NavType.IntType })) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            OwnerDetailScreen(id = id, onNavigateBack = { navController.popBackStack() }, onNavigateToEdit = { editId -> navController.navigate(Screen.OwnerForm.createRoute(editId)) }, onDeleted = { navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true); navController.popBackStack() })
        }
        composable(Screen.OwnerForm.route, arguments = listOf(navArgument("id") { type = NavType.StringType; nullable = true })) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            OwnerFormScreen(id = id, onNavigateBack = { navController.popBackStack() }, onSaved = { navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true) })
        }

        // Devices
        composable(Screen.DeviceList.route) { backStackEntry ->
            val needsRefresh = backStackEntry.savedStateHandle.get<Boolean>("refresh") == true
            val viewModel: DeviceViewModel = hiltViewModel()
            LaunchedEffect(needsRefresh) {
                if (needsRefresh) { viewModel.refreshList(); backStackEntry.savedStateHandle.remove<Boolean>("refresh") }
            }
            WithBottomNav(navController, Screen.DeviceList.route) {
                DeviceListScreen(
                    onNavigateToDetail = { id -> navController.navigate(Screen.DeviceDetail.createRoute(id)) },
                    onNavigateToCreate = { navController.navigate(Screen.DeviceForm.createRoute()) },
                    viewModel = viewModel
                )
            }
        }
        composable(Screen.DeviceDetail.route, arguments = listOf(navArgument("id") { type = NavType.IntType })) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            DeviceDetailScreen(id = id, onNavigateBack = { navController.popBackStack() }, onNavigateToEdit = { editId -> navController.navigate(Screen.DeviceForm.createRoute(editId)) }, onDeleted = { navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true); navController.popBackStack() })
        }
        composable(Screen.DeviceForm.route, arguments = listOf(navArgument("id") { type = NavType.StringType; nullable = true })) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            DeviceFormScreen(id = id, onNavigateBack = { navController.popBackStack() }, onSaved = { navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true) })
        }

        // Geofences
        composable(Screen.GeofenceList.route) { backStackEntry ->
            val needsRefresh = backStackEntry.savedStateHandle.get<Boolean>("refresh") == true
            val viewModel: GeofenceViewModel = hiltViewModel()
            LaunchedEffect(needsRefresh) {
                if (needsRefresh) { viewModel.refreshList(); backStackEntry.savedStateHandle.remove<Boolean>("refresh") }
            }
            WithBottomNav(navController, Screen.GeofenceList.route) {
                GeofenceListScreen(
                    onNavigateToDetail = { id -> navController.navigate(Screen.GeofenceDetail.createRoute(id)) },
                    onNavigateToCreate = { navController.navigate(Screen.GeofenceForm.createRoute()) },
                    viewModel = viewModel
                )
            }
        }
        composable(Screen.GeofenceDetail.route, arguments = listOf(navArgument("id") { type = NavType.IntType })) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            GeofenceDetailScreen(id = id, onNavigateBack = { navController.popBackStack() }, onNavigateToEdit = { editId -> navController.navigate(Screen.GeofenceForm.createRoute(editId)) }, onDeleted = { navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true); navController.popBackStack() })
        }
        composable(Screen.GeofenceForm.route, arguments = listOf(
            navArgument("id") { type = NavType.StringType; nullable = true },
            navArgument("petId") { type = NavType.StringType; nullable = true }
        )) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            val petId = backStackEntry.arguments?.getString("petId")?.toIntOrNull()
            GeofenceFormScreen(id = id, initialPetId = petId, onNavigateBack = { navController.popBackStack() }, onSaved = { navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true) })
        }

        // Alerts
        composable(Screen.AlertList.route) { backStackEntry ->
            val needsRefresh = backStackEntry.savedStateHandle.get<Boolean>("refresh") == true
            val viewModel: AlertViewModel = hiltViewModel()
            LaunchedEffect(needsRefresh) {
                if (needsRefresh) { viewModel.refreshList(); backStackEntry.savedStateHandle.remove<Boolean>("refresh") }
            }
            WithBottomNav(navController, Screen.AlertList.route) {
                AlertListScreen(
                    onNavigateToDetail = { id -> navController.navigate(Screen.AlertDetail.createRoute(id)) },
                    onNavigateToCreate = { navController.navigate(Screen.AlertForm.createRoute()) },
                    viewModel = viewModel
                )
            }
        }
        composable(Screen.AlertDetail.route, arguments = listOf(navArgument("id") { type = NavType.IntType })) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            AlertDetailScreen(id = id, onNavigateBack = { navController.popBackStack() }, onNavigateToEdit = { editId -> navController.navigate(Screen.AlertForm.createRoute(editId)) }, onDeleted = { navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true); navController.popBackStack() })
        }
        composable(Screen.AlertForm.route, arguments = listOf(
            navArgument("id") { type = NavType.StringType; nullable = true },
            navArgument("petId") { type = NavType.StringType; nullable = true }
        )) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            val petId = backStackEntry.arguments?.getString("petId")?.toIntOrNull()
            AlertFormScreen(id = id, initialPetId = petId, onNavigateBack = { navController.popBackStack() }, onSaved = { navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true) })
        }

        // Users
        composable(Screen.UserList.route) { backStackEntry ->
            val needsRefresh = backStackEntry.savedStateHandle.get<Boolean>("refresh") == true
            val viewModel: UserViewModel = hiltViewModel()
            LaunchedEffect(needsRefresh) {
                if (needsRefresh) { viewModel.refreshList(); backStackEntry.savedStateHandle.remove<Boolean>("refresh") }
            }
            WithBottomNav(navController, Screen.UserList.route) {
                UserListScreen(
                    onNavigateToDetail = { id -> navController.navigate(Screen.UserDetail.createRoute(id)) },
                    onNavigateToCreate = { navController.navigate(Screen.UserForm.createRoute()) },
                    viewModel = viewModel
                )
            }
        }
        composable(Screen.UserDetail.route, arguments = listOf(navArgument("id") { type = NavType.IntType })) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            UserDetailScreen(id = id, onNavigateBack = { navController.popBackStack() }, onNavigateToEdit = { editId -> navController.navigate(Screen.UserForm.createRoute(editId)) }, onDeleted = { navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true); navController.popBackStack() })
        }
        composable(Screen.UserForm.route, arguments = listOf(navArgument("id") { type = NavType.StringType; nullable = true })) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            UserFormScreen(id = id, onNavigateBack = { navController.popBackStack() }, onSaved = { navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true) })
        }

        // Map
        composable(Screen.MapScreen.route) {
            WithBottomNav(navController, Screen.MapScreen.route) {
                com.trackpets.app.presentation.map.MapScreen(
                    onNavigateToGeofence = { petId -> 
                        if (petId == null) {
                            navController.navigate(Screen.GeofenceForm.createRoute())
                        } else {
                            navController.navigate(Screen.GeofenceForm.createRoute(petId = petId))
                        }
                    }
                )
            }
        }

        // More
        composable(Screen.More.route) {
            WithBottomNav(navController, Screen.More.route) {
                com.trackpets.app.presentation.more.MoreScreen(
                    onNavigateToUsers = { navController.navigate(Screen.UserList.route) },
                    onNavigateToGeofences = { navController.navigate(Screen.GeofenceList.route) },
                    onNavigateToAlerts = { navController.navigate(Screen.AlertList.route) },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun WithBottomNav(
    navController: NavHostController,
    currentRoute: String,
    content: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                BottomNavItem.entries.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        alwaysShowLabel = false,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            content()
        }
    }
}
