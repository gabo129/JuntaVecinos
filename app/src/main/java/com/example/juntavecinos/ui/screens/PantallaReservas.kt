package com.example.juntavecinos.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.juntavecinos.model.ReservaRepository
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

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
    Column {

        Text(text = "Reservas")

        Button(onClick = onNuevaReservaClick) {
            Text(text = "Nueva Reserva")
        }
        if (reservas.isEmpty()) {
            Text(text = "No hay reservas registradas")
        } else {
            reservas.forEach { reserva ->
                Text(
                    text = "${reserva.nombre} - ${reserva.motivo}"
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

        Button(onClick = onVolverClick) {
            Text(text = "Volver")
        }
    }
}
