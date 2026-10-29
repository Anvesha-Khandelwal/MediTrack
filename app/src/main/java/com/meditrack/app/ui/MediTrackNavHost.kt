package com.meditrack.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.meditrack.app.data.MediTrackRepository
import com.meditrack.app.ui.screens.AddMedicineScreen
import com.meditrack.app.ui.screens.EditMedicineScreen
import com.meditrack.app.ui.screens.HistoryScreen
import com.meditrack.app.ui.screens.HomeScreen
import com.meditrack.app.ui.screens.MedicinesScreen
import com.meditrack.app.ui.screens.OnboardingScreen
import com.meditrack.app.ui.screens.ProfileScreen
import com.meditrack.app.viewmodel.MediTrackViewModelProvider
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun MediTrackNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val repository = remember { MediTrackRepository.getInstance(context) }
    val factory = remember { MediTrackViewModelProvider(repository) }

    // Check if onboarding is done. Default to false so first-launch users
    // see onboarding immediately rather than flashing the main screen.
    val onboardingDone by repository.onboardingDone.collectAsState(initial = false)

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Show bottom bar only on main screens
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Medicines.route,
        Screen.History.route,
        Screen.Profile.route
    )

    if (!onboardingDone) {
        OnboardingScreen(
            onDone = {
                // Setting this flag triggers recomposition; the NavHost
                // below will then compose with Home as the start destination.
                MainScope().launch {
                    repository.setOnboardingDone(true)
                }
            }
        )
        return
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                MediTrackBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                val homeViewModel = viewModel<com.meditrack.app.viewmodel.HomeViewModel>(factory = factory)
                HomeScreen(
                    viewModel = homeViewModel,
                    onAddMedicine = { navController.navigate("add_medicine") },
                    onViewSchedule = { navController.navigate("medicines") },
                    onMarkDose = { med, time, status ->
                        homeViewModel.recordDose(med, time, status)
                    },
                    onLoadDemoData = {
                        homeViewModel.loadDemoData()
                    }
                )
            }

            composable(Screen.Medicines.route) {
                MedicinesScreen(
                    viewModel = viewModel(factory = factory),
                    onAddMedicine = { navController.navigate("add_medicine") },
                    onEditMedicine = { id ->
                        navController.navigate("edit_medicine/$id")
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    viewModel = viewModel(factory = factory)
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel(factory = factory)
                )
            }

            composable("add_medicine") {
                AddMedicineScreen(
                    viewModel = viewModel(factory = factory),
                    onDone = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable(
                "edit_medicine/{medicineId}",
                arguments = listOf(navArgument("medicineId") { type = NavType.LongType })
            ) { entry ->
                val medicineId = entry.arguments?.getLong("medicineId") ?: 0L
                EditMedicineScreen(
                    viewModel = viewModel(factory = factory),
                    medicineId = medicineId,
                    onDone = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }
        }
    }
}
