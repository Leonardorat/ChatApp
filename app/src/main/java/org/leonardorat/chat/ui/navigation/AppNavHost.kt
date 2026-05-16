package org.leonardorat.chat.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.leonardorat.chat.auth.AuthManager
import org.leonardorat.chat.auth.AuthorizationRequiredException
import org.leonardorat.chat.messages.MessageRepository
import org.leonardorat.chat.profile.ProfileRepository
import org.leonardorat.chat.rooms.RoomRepository
import org.leonardorat.chat.ui.auth.AuthScreen
import org.leonardorat.chat.ui.chat.ChatScreen
import org.leonardorat.chat.ui.profile.ProfileSetupScreen
import org.leonardorat.chat.ui.rooms.ChatListScreen
import org.leonardorat.chat.ui.rooms.CreateRoomScreen

@Composable
fun AppNavHost(
    authManager: AuthManager,
    profileRepository: ProfileRepository,
    roomRepository: RoomRepository,
    messageRepository: MessageRepository
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    var status by remember { mutableStateOf("") }

    val rooms by roomRepository
        .observeRooms()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    NavHost(
        navController = navController,
        startDestination = Routes.AUTH_CHECK
    ) {
        composable(Routes.AUTH_CHECK) {
            LaunchedEffect(Unit) {
                val isAuthorized = authManager.ensureAuthorized()

                if (!isAuthorized) {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.AUTH_CHECK) { inclusive = true }
                    }
                    return@LaunchedEffect
                }

                val profile = profileRepository.getProfile()

                if (profile == null) {
                    navController.navigate(Routes.PROFILE) {
                        popUpTo(Routes.AUTH_CHECK) { inclusive = true }
                    }
                } else {
                    navController.navigate(Routes.ROOMS) {
                        popUpTo(Routes.AUTH_CHECK) { inclusive = true }
                    }
                }
            }

            androidx.compose.material3.Text("checking auth")
        }

        composable(Routes.AUTH) {
            AuthScreen(
                authManager = authManager,
                onAuthSuccess = {
                    navController.navigate(Routes.AUTH_CHECK) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PROFILE) {
            ProfileSetupScreen(
                status = status,
                onSaveProfile = { name ->
                    scope.launch {
                        try {
                            status = "creating profile..."
                            profileRepository.createProfile(name)
                            status = ""

                            navController.navigate(Routes.ROOMS) {
                                popUpTo(Routes.PROFILE) { inclusive = true }
                            }
                        } catch (e: AuthorizationRequiredException) {
                            status = ""
                            navController.navigate(Routes.AUTH) {
                                popUpTo(Routes.PROFILE) { inclusive = true }
                            }
                        } catch (e: Exception) {
                            status = "error: ${e::class.simpleName}: ${e.message}"
                        }
                    }
                }
            )
        }

        composable(Routes.ROOMS) {
            ChatListScreen(
                rooms = rooms,
                status = status,
                onCreateRoomClick = {
                    navController.navigate(Routes.CREATE_ROOM)
                },
                onRoomClick = { roomId ->
                    navController.navigate(Routes.chat(roomId))
                },
                onRefreshClick = {
                    scope.launch {
                        try {
                            status = "updating..."
                            messageRepository.syncLatestMessages()
                            status = "updated"
                        } catch (e: AuthorizationRequiredException) {
                            status = ""
                            navController.navigate(Routes.AUTH)
                        } catch (e: Exception) {
                            status = "error: ${e::class.simpleName}: ${e.message}"
                        }
                    }
                }
            )
        }

        composable(Routes.CREATE_ROOM) {
            CreateRoomScreen(
                status = status,
                onCreate = { name, email ->
                    scope.launch {
                        try {
                            status = "creating chat..."
                            val roomId = roomRepository.createRoom(name, email)
                            status = ""

                            navController.navigate(Routes.chat(roomId)) {
                                popUpTo(Routes.ROOMS)
                            }
                        } catch (e: Exception) {
                            status = "error: ${e::class.simpleName}: ${e.message}"
                        }
                    }
                }
            )
        }

        composable(
            route = Routes.CHAT,
            arguments = listOf(
                navArgument("roomId") { type = NavType.StringType }
            )
        ) { entry ->
            val roomId = entry.arguments?.getString("roomId")
                ?: error("roomId is missing")

            var roomTitle by remember(roomId) { mutableStateOf("chat") }

            LaunchedEffect(roomId) {
                roomTitle = roomRepository.getRoom(roomId).displayName
            }

            val messages by messageRepository
                .observeMessages(roomId)
                .collectAsStateWithLifecycle(initialValue = emptyList())

            ChatScreen(
                roomTitle = roomTitle,
                messages = messages,
                status = status,
                onSend = { text ->
                    scope.launch {
                        try {
                            status = "sending..."
                            messageRepository.sendMessage(roomId, text)
                            status = "sent"
                        } catch (e: AuthorizationRequiredException) {
                            status = ""
                            navController.navigate(Routes.AUTH)
                        } catch (e: Exception) {
                            status = "error: ${e::class.simpleName}: ${e.message}"
                        }
                    }
                }
            )
        }
    }
}