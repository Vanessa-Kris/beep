package com.example.beep

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.beep.ui.theme.BeepTheme
import ui.chat.ChatScreen
import ui.components.LocalLocale
import ui.components.LocaleHelper
import ui.inbox.InboxScreen
import ui.settings.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        val savedLanguage = LocaleHelper.getSavedLanguage(newBase)
        super.attachBaseContext(LocaleHelper.setLocale(newBase, savedLanguage))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val savedLanguage = LocaleHelper.getSavedLanguage(this)

        setContent {
            var currentLocale by remember { mutableStateOf(savedLanguage) }

            CompositionLocalProvider(LocalLocale provides currentLocale) {
                BeepTheme {
                    AppNavigation(
                        onLocaleChanged = { newLocale ->
                            LocaleHelper.setLocale(this, newLocale)
                            currentLocale = newLocale
                            recreate()
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun AppNavigation(onLocaleChanged: (String) -> Unit = {}) {
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
                onBackClick = { navController.popBackStack() },
                onLanguageChanged = onLocaleChanged
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