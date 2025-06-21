package com.example.firebase.screen.auth

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.firebase.data.firebase.FirebaseAuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Título
        Text(
            text = "Registro",
        )

        // Campo para ingresar el nombre completo
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Nombre Completo") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp)
        )

        // Campo para ingresar el correo electrónico
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp)
        )

        // Campo para ingresar la contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp),
            visualTransformation = PasswordVisualTransformation()
        )

        // Campo para confirmar la contraseña
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Repetir Contraseña") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp),
            visualTransformation = PasswordVisualTransformation()
        )

        // Botón de Registrarse
        Button(
            onClick = {
                if (password == confirmPassword && fullName.isNotBlank()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val result = FirebaseAuthManager.registerUser(fullName, email, password)
                        if (result.isSuccess) {
                            navController.navigate("login")
                        } else {
                            val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(bottom = 16.dp)
        ) {
            Text(
                "Registrarse",
                style = TextStyle(color = Color.White, fontSize = 16.sp)
            )
        }

        // Botón de Iniciar Sesión
        TextButton(
            onClick = {
                navController.navigate("login")
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("¿Ya tienes cuenta? Inicia sesión", style = TextStyle(color = Color(0xFF6200EE), fontSize = 14.sp))
        }
    }
}
