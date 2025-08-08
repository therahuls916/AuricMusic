// In ui/screens/MainAppScreen.kt

package com.rahul.auricmusic.ui.screens
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rahul.auricmusic.navigation.BottomNavItem
import com.rahul.auricmusic.player.MiniPlayer
import com.rahul.auricmusic.player.NowPlayingScreen
import com.rahul.auricmusic.ui.screens.favorites.FavoritesScreen
import com.rahul.auricmusic.ui.screens.home.HomeScreen
import com.rahul.auricmusic.viewmodel.MainViewModel
import com.rahul.auricmusic.ui.theme.RaisinBlack
import com.rahul.auricmusic.ui.screens.playlists.PlaylistsScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.rahul.auricmusic.ui.screens.playlists.PlaylistDetailsScreen
import com.rahul.auricmusic.ui.screens.profile.ProfileScreen


@Composable
fun MainAppScreen() {
    val navController = rememberNavController()
    // The MainViewModel is created ONCE here and passed down to all children.
    val mainViewModel: MainViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "main_with_bottom_bar"
    ) {
        composable("main_with_bottom_bar") {
            MainScaffold(
                mainViewModel = mainViewModel,
                navigateToPlayer = { navController.navigate("now_playing") },
                // NEW: Navigate to playlist details
                navigateToPlaylistDetails = { playlistId ->
                    navController.navigate("playlist_details/$playlistId")
                }
            )
        }
        composable("now_playing") {
            NowPlayingScreen(
                mainViewModel = mainViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        // NEW DESTINATION
        composable(
            route = "playlist_details/{playlistId}",
            arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L
            PlaylistDetailsScreen(
                playlistId = playlistId,
                mainViewModel = mainViewModel,
                homeViewModel = viewModel(),
                playlistsViewModel = viewModel(),
                onBackClick = { navController.popBackStack() },
                onSongClick = { navController.navigate("now_playing") }
            )
        }
    }
}

/**
 * This composable builds the main UI container with the Scaffold and bottom bar.
 * It ACCEPTS the MainViewModel, it does not create it.
 */
@Composable
private fun MainScaffold(
    mainViewModel: MainViewModel, // It receives the ViewModel
    navigateToPlayer: () -> Unit,
    navigateToPlaylistDetails: (Long) -> Unit
) {
    // It does NOT call viewModel() again. It uses the one that was passed in.
    val playbackState by mainViewModel.playbackState.collectAsState()
    val bottomNavController = rememberNavController()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            Column (
                modifier = Modifier.windowInsetsPadding(NavigationBarDefaults.windowInsets)
            ){
                playbackState.currentPlayingSong?.let { song ->
                    Box(modifier = Modifier.clickable { navigateToPlayer() }) {
                        MiniPlayer(
                            song = song,
                            isPlaying = playbackState.isPlaying,
                            onPlayPauseClick = {
                                if (playbackState.isPlaying) mainViewModel.pause() else mainViewModel.resume()
                            }
                        )
                    }
                }
                AppNavigationBar(navController = bottomNavController)
            }
        }
    ) { innerPadding ->
        BottomNavGraph(
            modifier = Modifier
                .padding(innerPadding)
                .safeDrawingPadding(),
            navController = bottomNavController,
            mainViewModel = mainViewModel, // Pass the ViewModel down to the next level
            onSongClick = navigateToPlayer,
            onPlaylistClick = navigateToPlaylistDetails
        )
    }
}

/**
 * This is the NavHost for the screens controlled by the bottom bar.
 * It also accepts the MainViewModel and passes it to its children.
 */
@Composable
private fun BottomNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    mainViewModel: MainViewModel, // It receives the ViewModel
    onSongClick: () -> Unit,
    onPlaylistClick: (Long) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Home.route) {
            // Pass the single ViewModel instance to HomeScreen
            HomeScreen(mainViewModel = mainViewModel, onSongClick = onSongClick)
        }
        composable(BottomNavItem.Favorites.route) {
            // Pass the single ViewModel instance to FavoritesScreen
            FavoritesScreen(mainViewModel = mainViewModel, onSongClick = onSongClick)
        }
        composable(BottomNavItem.Playlists.route) {
            PlaylistsScreen(onPlaylistClick = onPlaylistClick) // <-- PASS IT HERE
        }
        composable(BottomNavItem.Profile.route) { ProfileScreen()}
    }
}


/**
 * The NavigationBar UI. It does not need the ViewModel.
 */
@Composable
private fun AppNavigationBar(navController: NavHostController) {
    NavigationBar (
        containerColor = RaisinBlack,
        tonalElevation = 0.dp

    ){
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.Playlists,
            BottomNavItem.Favorites,
            BottomNavItem.Profile
        )
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary, // Goldenrod
                    selectedTextColor = MaterialTheme.colorScheme.primary, // Goldenrod
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant, // LightKhaki
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant, // LightKhaki
                    indicatorColor = Color.Transparent // This removes the pill-shaped background
                )
            )
        }
    }
}