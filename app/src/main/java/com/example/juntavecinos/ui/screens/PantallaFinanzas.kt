package com.example.juntavecinos.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.juntavecinos.data.repository.MovimientoRepository
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
fun PantallaFinanzas(
    repository: MovimientoRepository,
    onVolverClick: () -> Unit,
    onNuevoMovimientoClick: () -> Unit = {}
) {
    val movimientos by repository.movimientos.collectAsState(
        initial = emptyList()
    )
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(text = "Finanzas")

        Button(onClick = onNuevoMovimientoClick) {
            Text(text = "Registrar movimiento")
        }
        val totalIngresos = movimientos
            .filter { it.esIngreso }
            .sumOf { it.monto }

        val totalGastos = movimientos
            .filter { !it.esIngreso }
            .sumOf { it.monto }

        val saldo = totalIngresos - totalGastos

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Resumen financiero",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(text = "Total ingresos: $$totalIngresos")
                Text(text = "Total gastos: $$totalGastos")

                Text(
                    text = "Saldo disponible: $$saldo",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        if (movimientos.isEmpty()) {
            Text(text = "No hay movimientos registrados")
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
                        Text(
                            text = if (movimiento.esIngreso) "Ingreso" else "Gasto",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "Descripción: ${movimiento.descripcion}"
                        )

                        val formatoFecha = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        )
                        formatoFecha.timeZone = TimeZone.getTimeZone("UTC")

                        Text(
                            text = "Fecha: ${formatoFecha.format(Date(movimiento.fecha))}"
                        )

                        Text(
                            text = "Monto: $${movimiento.monto}"
                        )

                        Button(
                            onClick = {
                                scope.launch {
                                    repository.eliminarMovimiento(movimiento)
                                }
                            }
                        ) {
                            Text("Eliminar")
                        }
                    }
                }
            }

            Button(onClick = onVolverClick) {
                Text(text = "Volver")
            }
        }
    }
}