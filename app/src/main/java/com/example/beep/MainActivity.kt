package com.example.beep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.beep.ui.theme.BeepTheme
import ui.chat.ChatScreen
import ui.inbox.InboxScreen
import ui.settings.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BeepTheme {
                AppNavigation()
                }
            }
        }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "inbox") {
        composable("inbox") {
            InboxScreen(
                onThreadClick = { threadName ->
                    navController.navigate("chat/$threadName")
                },
                navController = navController
            )
        }
        composable("chat/{threadName}") { backStackEntry ->
            val threadName = backStackEntry.arguments?.getString("threadName") ?: ""
            ChatScreen(
                threadName = threadName,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("settings") {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BeepTheme {
        AppNavigation()
    }
}