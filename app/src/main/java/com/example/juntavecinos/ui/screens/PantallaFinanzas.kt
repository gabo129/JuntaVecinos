package com.example.juntavecinos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.juntavecinos.data.remote.FinanzasRetrofitClient
import com.example.juntavecinos.data.repository.MovimientoRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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

    var mensaje by remember {
        mutableStateOf("")
    }

    var eliminando by remember {
        mutableStateOf(false)
    }

    val totalIngresos = movimientos
        .filter { it.esIngreso }
        .sumOf { it.monto }

    val totalGastos = movimientos
        .filter { !it.esIngreso }
        .sumOf { it.monto }

    val saldo = totalIngresos - totalGastos

    val formatoMes = SimpleDateFormat("MM/yyyy", Locale.getDefault())
    formatoMes.timeZone = TimeZone.getTimeZone("UTC")

    val mesActual = formatoMes.format(Date())

    var mesSeleccionado by remember {
        mutableStateOf(mesActual)
    }

    val movimientosMes = movimientos.filter {
        formatoMes.format(Date(it.fecha)) == mesSeleccionado
    }

    val ingresosMes = movimientosMes
        .filter { it.esIngreso }
        .sumOf { it.monto }

    val gastosMes = movimientosMes
        .filter { !it.esIngreso }
        .sumOf { it.monto }

    val saldoMes = ingresosMes - gastosMes

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

        MovimientosServidor()

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

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Resumen mensual: $mesSeleccionado",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val calendario = Calendar.getInstance(
                                TimeZone.getTimeZone("UTC")
                            )
                            calendario.time =
                                formatoMes.parse(mesSeleccionado) ?: Date()

                            calendario.add(Calendar.MONTH, -1)
                            mesSeleccionado =
                                formatoMes.format(calendario.time)
                        }
                    ) {
                        Text("Anterior")
                    }

                    Button(
                        onClick = {
                            val calendario = Calendar.getInstance(
                                TimeZone.getTimeZone("UTC")
                            )
                            calendario.time =
                                formatoMes.parse(mesSeleccionado) ?: Date()

                            calendario.add(Calendar.MONTH, 1)
                            mesSeleccionado =
                                formatoMes.format(calendario.time)
                        }
                    ) {
                        Text("Siguiente")
                    }
                }

                Text("Ingresos del mes: $$ingresosMes")
                Text("Gastos del mes: $$gastosMes")

                Text(
                    text = "Saldo del mes: $$saldoMes",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        if (mensaje.isNotEmpty()) {
            Text(
                text = mensaje,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (movimientos.isEmpty()) {
            Text("No hay movimientos registrados")
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

                        Text("Descripción: ${movimiento.descripcion}")
                        Text("Categoría: ${movimiento.categoria}")

                        val formatoFecha = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        )
                        formatoFecha.timeZone =
                            TimeZone.getTimeZone("UTC")

                        Text(
                            text = "Fecha: ${formatoFecha.format(Date(movimiento.fecha))}"
                        )

                        Text("Monto: $${movimiento.monto}")

                        Button(
                            enabled = !eliminando,
                            onClick = {
                                scope.launch {
                                    eliminando = true
                                    mensaje = ""

                                    try {
                                        val idServidor = movimiento.servidorId

                                        if (idServidor == null) {
                                            mensaje = "Este movimiento no tiene ID del servidor. No se eliminó."
                                        } else {
                                            val respuesta =
                                                FinanzasRetrofitClient.finanzasApi
                                                    .eliminarMovimiento(idServidor)

                                            if (respuesta.isSuccessful) {
                                                repository.eliminarMovimiento(movimiento)
                                                mensaje = "Movimiento eliminado de MySQL y Room"
                                            } else {
                                                mensaje = "No se pudo eliminar en MySQL"
                                            }
                                        }

                                    } catch (e: Exception) {
                                        mensaje = "Error al conectar con el servidor"
                                    } finally {
                                        eliminando = false
                                    }
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
            Text("Volver")
        }
    }
}
