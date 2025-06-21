package com.example.firebase.data.firebase

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object CurrencyManager {
    private val firestore = FirebaseFirestore.getInstance()

    // Lista de monedas y sus tasas predeterminadas
    private val defaultCurrencies = mapOf(
        "USD" to 1.0,    // US Dollar
        "EUR" to 0.85,   // Euro
        "PEN" to 3.7,    // Sol Peruano
        "GBP" to 0.75,   // British Pound
        "JPY" to 110.0   // Japanese Yen
    )

    // Función para agregar monedas a Firestore
    suspend fun addCurrencyToFirestore(currency: String, rate: Double): Result<Unit> {
        return try {
            val currencyData = hashMapOf(
                "Tipo Moneda" to currency,
                "tasa" to rate
            )

            // Agregar el documento a la colección "monedas"
            firestore.collection("monedas").document(currency).set(currencyData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Función para verificar y agregar las monedas predeterminadas
    suspend fun initializeCurrenciesIfNeeded() {
        try {
            // Comprobar si ya existen monedas en Firestore
            val currencies = firestore.collection("monedas").get().await()

            if (currencies.isEmpty) {
                // Si no hay monedas, agregar las predeterminadas
                for ((currency, rate) in defaultCurrencies) {
                    addCurrencyToFirestore(currency, rate)
                }
            }
        } catch (e: Exception) {
            // Manejo de errores en caso de fallo al leer o escribir en Firestore
            println("Error inicializando monedas: ${e.message}")
        }
    }

    // Función para obtener la tasa de conversión entre dos monedas
    suspend fun getConversionRate(fromCurrency: String, toCurrency: String): Result<Double> {
        return try {
            // Obtener la tasa de la moneda de origen
            val fromCurrencyDoc = firestore.collection("monedas").document(fromCurrency).get().await()
            val fromRate = fromCurrencyDoc.getDouble("tasa") ?: return Result.failure(Exception("No se encontró la tasa de $fromCurrency"))

            // Obtener la tasa de la moneda de destino
            val toCurrencyDoc = firestore.collection("monedas").document(toCurrency).get().await()
            val toRate = toCurrencyDoc.getDouble("tasa") ?: return Result.failure(Exception("No se encontró la tasa de $toCurrency"))

            // Calcular el tipo de cambio entre las monedas
            val conversionRate = fromRate / toRate
            Result.success(conversionRate)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Función para realizar la conversión de un monto de una moneda a otra
    suspend fun convertAmount(amount: Double, fromCurrency: String, toCurrency: String): Result<Double> {
        val conversionRateResult = getConversionRate(fromCurrency, toCurrency)

        return if (conversionRateResult.isSuccess) {
            val conversionRate = conversionRateResult.getOrNull() ?: return Result.failure(Exception("Error en la conversión"))
            val convertedAmount = amount * conversionRate
            Result.success(convertedAmount)
        } else {
            Result.failure(conversionRateResult.exceptionOrNull() ?: Exception("Error en la conversión"))
        }
    }

    // Función para guardar la conversión realizada en Firestore
    fun saveConversion(userId: String, amount: Double, fromCurrency: String, toCurrency: String, result: Double) {
        val conversionData = hashMapOf(
            "UID" to userId,
            "Fecha/Hora" to FieldValue.serverTimestamp(),
            "Monto" to amount,
            "Moneda de origen" to fromCurrency,
            "Moneda de destino" to toCurrency,
            "Resultado" to result
        )

        firestore.collection("conversiones")
            .add(conversionData)
            .addOnSuccessListener {
                // Conversión guardada exitosamente
            }
            .addOnFailureListener { e ->
                // Error al guardar la conversión
            }
    }
}
