package com.example.firebase.screen.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.firebase.data.firebase.FirebaseAuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) } // Estado de carga

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()  // Asegura que la columna ocupe toda la pantalla
            .padding(16.dp),  // Mantiene el espacio de padding
        horizontalAlignment = Alignment.CenterHorizontally,  // Centrado horizontal
        verticalArrangement = Arrangement.Center

    ) {
        Spacer(modifier = Modifier.padding(10.dp))
        Text("Inicio de sesión", style = MaterialTheme.typography.titleLarge)

        // Campo de entrada para correo electrónico
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo de entrada para contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        // Botón para iniciar sesión
        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    isLoading = true // Inicia el indicador de carga
                    CoroutineScope(Dispatchers.Main).launch {
                        val result = FirebaseAuthManager.loginUser(email, password)
                        isLoading = false // Detiene el indicador de carga

                        if (result.isSuccess) {
                            navController.navigate("home")
                        } else {
                            val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Por favor, ingresa tu correo y contraseña", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Iniciar Sesión")
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }
    }
}
