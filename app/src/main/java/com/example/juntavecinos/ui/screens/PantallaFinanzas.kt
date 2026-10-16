package com.example.juntavecinos.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.juntavecinos.model.MovimientoRepository
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
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
    Column {
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

        Text(text = "Total ingresos: $$totalIngresos")
        Text(text = "Total gastos: $$totalGastos")
        Text(text = "Saldo disponible: $$saldo")

        if (movimientos.isEmpty()) {
            Text(text = "No hay movimientos registrados")
        } else {
            movimientos.forEach { movimiento ->
                Text(
                    text = if (movimiento.esIngreso) "Ingreso" else "Gasto"
                )

                Text(
                    text = "Descripción: ${movimiento.descripcion}"
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

        Button(onClick = onVolverClick) {
            Text(text = "Volver")
        }
    }
}