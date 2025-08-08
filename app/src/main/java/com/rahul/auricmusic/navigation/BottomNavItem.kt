// In navigation/BottomNavItem.kt

package com.rahul.auricmusic.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )
    object Playlists : BottomNavItem(
        route = "playlists",
        title = "Playlists",
        icon = Icons.AutoMirrored.Filled.List
    )
    object Favorites : BottomNavItem(
        route = "favorites",
        title = "Favorites",
        icon = Icons.Default.Favorite
    )
    object Profile : BottomNavItem(
        route = "profile",
        title = "Profile",
        icon = Icons.Default.Person
    )
}