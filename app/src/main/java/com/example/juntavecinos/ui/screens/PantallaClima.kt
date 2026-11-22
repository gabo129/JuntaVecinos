package com.example.juntavecinos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.juntavecinos.data.model.ClimaResponse
import com.example.juntavecinos.data.remote.RetrofitClient

@Composable
fun PantallaClima(
    onVolverClick: () -> Unit
) {
    var clima by remember {
        mutableStateOf<ClimaResponse?>(null)
    }

    var cargando by remember {
        mutableStateOf(true)
    }

    var error by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        try {
            clima = RetrofitClient.climaApi.obtenerClima()
        } catch (e: Exception) {
            error = "No se pudo consultar el clima. Revisa tu conexión."
        } finally {
            cargando = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Clima de la comunidad",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Pronóstico para Santiago")

        if (cargando) {
            Text("Consultando el clima...")
        } else if (error.isNotEmpty()) {
            Text(error)
        } else {
            clima?.let { datos ->
                Text(
                    text = "Temperatura actual: ${datos.current.temperatura} °C",
                    style = MaterialTheme.typography.titleLarge
                )

                val lluvia = datos.daily.probabilidadLluvia.firstOrNull()

                if (lluvia != null) {
                    Text(
                        text = "Probabilidad máxima de lluvia hoy: $lluvia%"
                    )
                } else {
                    Text("Pronóstico de lluvia no disponible")
                }

                Text(
                    text = "Consulta el clima antes de organizar tus actividades comunitarias."
                )
            }
        }

        Button(onClick = onVolverClick) {
            Text("Volver")
        }
    }
}


