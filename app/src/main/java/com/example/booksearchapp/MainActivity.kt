package com.example.booksearchapp

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.doOnLayout
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.booksearchapp.presentation.navigation.BottomBar
import com.example.booksearchapp.presentation.navigation.NavGraph
import com.example.linguareader.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                // Обновление системных панелей после рендера
                DisposableEffect(Unit) {
                    updateSystemBars()
                    onDispose { }
                }

                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry?.destination?.route ?: ""

                Scaffold(
                    bottomBar = {
                        if (!currentRoute.startsWith("detail")) {
                            BottomBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun updateSystemBars() {
        // Установка белого цвета для статус-бара
        window.statusBarColor = Color.White.toArgb()

        // Настройка черных иконок
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true // Черные иконки
            isAppearanceLightNavigationBars = true // Для навигационной панели тоже
        }
    }
}