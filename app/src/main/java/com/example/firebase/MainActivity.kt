package com.example.firebase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.firebase.data.firebase.CurrencyManager
import com.example.firebase.screen.navegation.NavigationMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            CurrencyManager.initializeCurrenciesIfNeeded()
        }

        enableEdgeToEdge()

        setContent {
            // Comienza la navegación de la aplicación
            NavigationMenu()
        }
    }
}
