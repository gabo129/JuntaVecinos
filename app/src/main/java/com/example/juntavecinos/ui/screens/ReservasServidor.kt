package com.example.juntavecinos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.juntavecinos.data.model.ReservaRemota
import com.example.juntavecinos.data.remote.ReservasRetrofitClient
import retrofit2.HttpException

@Composable
fun ReservasServidor(autorizacion: String) {

    var reservas by remember {
        mutableStateOf<List<ReservaRemota>>(emptyList())
    }

    var mensaje by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    var actualizar by remember { mutableIntStateOf(0) }

    LaunchedEffect(autorizacion, actualizar) {
        cargando = true
        mensaje = ""

        try {
            reservas = ReservasRetrofitClient.reservaApi
                .listarReservas(autorizacion)

        } catch (e: HttpException) {
            reservas = emptyList()

            mensaje = when (e.code()) {
                401 -> "Debes iniciar sesión nuevamente"
                403 -> "No tienes permiso para ver las reservas"
                else -> "Error del servidor: ${e.code()}"
            }

        } catch (e: Exception) {
            reservas = emptyList()
            mensaje = "No se pudo conectar con el servidor"

        } finally {
            cargando = false
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Text(
            text = "Reservas desde MySQL",
            style = MaterialTheme.typography.titleLarge
        )

        Button(onClick = { actualizar++ }) {
            Text("Actualizar")
        }

        if (cargando) {
            CircularProgressIndicator()
        } else if (mensaje.isNotEmpty()) {
            Text(mensaje)
        } else if (reservas.isEmpty()) {
            Text("No tienes reservas registradas en MySQL")
        } else {
            reservas.forEach { reserva ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = reserva.dependencia,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text("Fecha: ${reserva.fecha}")

                        Text(
                            "Horario: ${reserva.horaInicio} a ${reserva.horaFin}"
                        )

                        Text(
                            "Responsable: ${reserva.responsable ?: "Sin especificar"}"
                        )

                        Text(
                            "Motivo: ${reserva.motivo ?: "Sin especificar"}"
                        )

                        Text("Estado: ${reserva.estado}")
                    }
                }
            }
        }
    }
}
