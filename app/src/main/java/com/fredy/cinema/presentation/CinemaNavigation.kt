package com.fredy.cinema.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fredy.cinema.presentation.rooms.CreateRoomScreen
import com.fredy.cinema.presentation.rooms.RoomsListScreen
import com.fredy.cinema.presentation.seats.SeatsScreen
import com.fredy.cinema.presentation.settings.SettingsScreen

sealed class Screen(val route: String) {
    object RoomsList : Screen("rooms_list")
    object CreateRoom : Screen("create_room")
    object RoomDetail : Screen("room_detail/{roomId}") {
        fun createRoute(roomId: String) = "room_detail/$roomId"
    }
    object Settings : Screen("settings")
}

@Composable
fun CinemaNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.RoomsList.route
    ) {
        composable(Screen.RoomsList.route) {
            RoomsListScreen(
                onRoomClick = { roomId ->
                    navController.navigate(Screen.RoomDetail.createRoute(roomId))
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onCreateRoomClick = {
                    navController.navigate(Screen.CreateRoom.route)
                }
            )
        }
        composable(Screen.CreateRoom.route) {
            CreateRoomScreen(
                onRoomCreated = { roomId ->
                    navController.navigate(Screen.RoomDetail.createRoute(roomId)) {
                        popUpTo(Screen.RoomsList.route)
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.RoomDetail.route,
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) {
            SeatsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
