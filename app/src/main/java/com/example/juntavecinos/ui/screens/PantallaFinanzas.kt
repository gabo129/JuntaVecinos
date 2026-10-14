package com.example.juntavecinos.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun PantallaFinanzas(
    onVolverClick: () -> Unit
) {
    Column {
        Text(text = "Finanzas")

        Button(onClick = onVolverClick) {
            Text(text = "Volver")
        }
    }
}