package com.example.juntavecinos.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import com.example.juntavecinos.data.model.MovimientoFinancieroApi
import com.example.juntavecinos.data.remote.FinanzasRetrofitClient

@Composable
fun MovimientosServidor() {

    var movimientos by remember {
        mutableStateOf<List<MovimientoFinancieroApi>>(emptyList())
    }

    var cargando by remember {
        mutableStateOf(true)
    }

    var error by remember {
        mutableStateOf("")
    }

    var actualizar by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(actualizar) {

        cargando = true
        error = ""

        try {
            movimientos = FinanzasRetrofitClient.finanzasApi.obtenerMovimientos()
        } catch (e: Exception) {
            error = "No se pudo conectar con el servidor"
        } finally {
            cargando = false
        }
    }

    Column {

        Text(
            text = "Movimientos desde MySQL",
            style = MaterialTheme.typography.titleLarge
        )

        Button(onClick = { actualizar++ }) {
            Text("Actualizar")
        }

        if (cargando) {
            Text("Cargando movimientos...")
        } else if (error.isNotEmpty()) {
            Text(error)
        } else if (movimientos.isEmpty()) {
            Text("No hay movimientos en MySQL")
        } else {
            movimientos.forEach { movimiento ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Tipo: ${movimiento.tipo}")
                        Text("Categoría: ${movimiento.categoria}")
                        Text("Descripción: ${movimiento.descripcion}")
                        Text("Monto: ${movimiento.monto} CLP")
                        Text("Fecha: ${movimiento.fecha}")
                    }
                }
            }
        }
    }
}
