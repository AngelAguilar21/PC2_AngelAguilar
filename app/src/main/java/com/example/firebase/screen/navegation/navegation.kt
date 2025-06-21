package com.example.firebase.screen.navegation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.firebase.screen.auth.LoginScreen
import com.example.firebase.screen.auth.RegisterScreen
import com.example.firebase.screen.home.HomeScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable



@Composable
fun NavigationMenu() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "register") {
        composable("register") { RegisterScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("home"){ HomeScreen(navController) }
    }
}
