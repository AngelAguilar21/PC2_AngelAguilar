package com.example.firebase.screen.home

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.firebase.data.firebase.CurrencyManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var amount by remember { mutableStateOf("") }
    var fromCurrency by remember { mutableStateOf("USD") }
    var toCurrency by remember { mutableStateOf("EUR") }
    var result by remember { mutableStateOf(0.0) }
    var expandedFrom by remember { mutableStateOf(false) }
    var expandedTo by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val currencies = listOf("USD", "EUR", "PEN", "GBP", "JPY")

    Column(
        modifier = Modifier
            .fillMaxSize()  // Asegura que la columna ocupe toda la pantalla
            .padding(16.dp),  // Mantiene el espacio de padding
        horizontalAlignment = Alignment.CenterHorizontally,  // Centrado horizontal
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.padding(10.dp))
        Text("Convertir Monedas", style = MaterialTheme.typography.titleLarge)

        // Campo para ingresar el monto a convertir
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Monto") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        // Selector de moneda de origen (DropdownMenu)
        Text("Moneda de Origen")
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expandedFrom,
                onExpandedChange = { expandedFrom = !expandedFrom }
            ) {
                TextField(
                    value = fromCurrency,
                    onValueChange = { },
                    label = { Text("Selecciona Moneda de Origen") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Flecha de despliegue"
                        )
                    },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedFrom,
                    onDismissRequest = { expandedFrom = false }
                ) {
                    currencies.forEach { currency ->
                        DropdownMenuItem(
                            text = { Text(text = currency) },
                            onClick = {
                                fromCurrency = currency
                                expandedFrom = false
                            }
                        )
                    }
                }
            }
        }

        // Selector de moneda de destino (DropdownMenu)
        Text("Moneda de Destino")
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expandedTo,
                onExpandedChange = { expandedTo = !expandedTo }
            ) {
                TextField(
                    value = toCurrency,
                    onValueChange = { },
                    label = { Text("Selecciona Moneda de Destino") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Flecha de despliegue"
                        )
                    },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedTo,
                    onDismissRequest = { expandedTo = false }
                ) {
                    currencies.forEach { currency ->
                        DropdownMenuItem(
                            text = { Text(text = currency) },
                            onClick = {
                                toCurrency = currency // Cambié 'fromCurrency' por 'toCurrency'
                                expandedTo = false
                            }
                        )
                    }
                }
            }
        }

        // Botón para realizar la conversión
        Button(
            onClick = {
                if (amount.isNotEmpty() && amount.toDoubleOrNull() != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        // Realizar la conversión
                        val conversionResult = CurrencyManager.convertAmount(amount.toDouble(), fromCurrency, toCurrency)

                        if (conversionResult.isSuccess) {
                            result = conversionResult.getOrNull() ?: 0.0
                            // Guardar la conversión realizada en Firestore
                            CurrencyManager.saveConversion(userId, amount.toDouble(), fromCurrency, toCurrency, result)
                            Toast.makeText(context, "Conversión realizada: $amount $fromCurrency equivalen a $result $toCurrency", Toast.LENGTH_LONG).show()
                        } else {
                            val error = conversionResult.exceptionOrNull()?.message ?: "Error en la conversión"
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Por favor, ingresa un monto válido", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Convertir")
        }

        Spacer(modifier = Modifier.padding(10.dp))

        // Mostrar el resultado de la conversión
        Text("Resultado: $result")
    }
}
