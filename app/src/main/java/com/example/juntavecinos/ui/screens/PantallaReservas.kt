package com.example.juntavecinos.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.juntavecinos.data.repository.ReservaRepository
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.fillMaxSize

@Composable
fun PantallaReservas(
    repository: ReservaRepository,
    onVolverClick: () -> Unit,
    onNuevaReservaClick: () -> Unit = {}
) {
    val reservas by repository.reservas.collectAsState(
        initial = emptyList()
    )
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Text(text = "Reservas")

        Button(onClick = onNuevaReservaClick) {
            Text(text = "Nueva Reserva")
        }
        if (reservas.isEmpty()) {
            Text(text = "No hay reservas registradas")
        } else {
            reservas.forEach { reserva ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = reserva.motivo,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(text = "Responsable: ${reserva.nombre}")
                        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        formatoFecha.timeZone = TimeZone.getTimeZone("UTC")

                        Text(
                            text = "Fecha: ${formatoFecha.format(Date(reserva.fecha))}"
                        )

                        Text(
                            text = "Horario: ${reserva.horaInicio} a ${reserva.horaFin}"
                        )

                        Button(
                            onClick = {
                                scope.launch {
                                    repository.eliminarReserva(reserva)
                                }
                            }
                        ) {
                            Text("Eliminar")
                        }
                    }
                }
            }
        }

        Button(onClick = onVolverClick) {
            Text(text = "Volver")
        }
    }
}
