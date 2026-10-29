package com.example.rodlayout.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rodlayout.ui.theme.IndustrialOrange
import com.example.rodlayout.ui.theme.RodLayoutTheme
import com.example.rodlayout.ui.viewmodel.AppTheme
import com.example.rodlayout.ui.viewmodel.RodViewModel
import com.example.rodlayout.ui.viewmodel.SettingsViewModel

enum class MainDestination(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    HOME("Calculator", Icons.Default.Calculate),
    RESULTS("Results", Icons.AutoMirrored.Filled.ListAlt),
    SETTINGS("Settings", Icons.Default.Settings),
    ABOUT("About", Icons.Default.Info)
}

@Composable
fun MainScreen(
    rodViewModel: RodViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    var showSplash by rememberSaveable { mutableStateOf(true) }
    var currentDestination by rememberSaveable { mutableStateOf(MainDestination.HOME) }

    val settingsState by settingsViewModel.uiState.collectAsState()
    val isSystemDark = isSystemInDarkTheme()
    val darkTheme = when (settingsState.theme) {
        AppTheme.SYSTEM -> isSystemDark
        AppTheme.DARK -> true
        AppTheme.LIGHT -> false
    }

    RodLayoutTheme(darkTheme = darkTheme) {
        if (showSplash) {
            SplashScreen(
                onSplashFinished = { showSplash = false }
            )
        } else {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        MainDestination.entries.forEach { destination ->
                            val isSelected = currentDestination == destination
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { currentDestination = destination },
                                icon = {
                                    Icon(
                                        imageVector = destination.icon,
                                        contentDescription = destination.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = destination.title,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = IndustrialOrange,
                                    selectedTextColor = IndustrialOrange,
                                    indicatorColor = IndustrialOrange.copy(alpha = 0.15f)
                                )
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                when (currentDestination) {
                    MainDestination.HOME -> {
                        HomeScreen(
                            viewModel = rodViewModel,
                            onNavigateToResults = { currentDestination = MainDestination.RESULTS },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }

                    MainDestination.RESULTS -> {
                        ResultsScreen(
                            rodViewModel = rodViewModel,
                            settingsViewModel = settingsViewModel,
                            onBack = { currentDestination = MainDestination.HOME },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }

                    MainDestination.SETTINGS -> {
                        SettingsScreen(
                            settingsViewModel = settingsViewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }

                    MainDestination.ABOUT -> {
                        AboutScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
