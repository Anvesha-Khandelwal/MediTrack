package com.meditrack.app.ui

/**
 * Navigation destinations for the bottom nav bar.
 */
sealed class Screen(val route: String, val label: String) {
    data object Home : Screen("home", "Home")
    data object Medicines : Screen("medicines", "Medicines")
    data object History : Screen("history", "History")
    data object Profile : Screen("profile", "Profile")
}

val bottomNavScreens = listOf(Screen.Home, Screen.Medicines, Screen.History, Screen.Profile)
