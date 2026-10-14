package com.example.juntavecinos.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun PantallaReservas(
    onVolverClick: () -> Unit,
    onNuevaReservaClick: () -> Unit = {}
) {
    Column {

        Text(text = "Reservas")

        Button(onClick = onNuevaReservaClick) {
            Text(text = "Nueva Reserva")
        }

        Button(onClick = onVolverClick) {
            Text(text = "Volver")
        }
    }
}
