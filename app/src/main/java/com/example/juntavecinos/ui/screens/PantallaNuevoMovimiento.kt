package com.example.juntavecinos.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.juntavecinos.viewmodel.MovimientoViewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.ui.Alignment
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import com.example.juntavecinos.model.MovimientoRepository
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaNuevoMovimiento(
    repository: MovimientoRepository,
    onVolverClick: () -> Unit,
    movimientoViewModel: MovimientoViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                MovimientoViewModel(repository)
            }
        }
    )
) {
    val datos by movimientoViewModel.movimiento.collectAsState()
    var errores by remember {
        mutableStateOf<Map<String, String>>(emptyMap())
    }

    var intentoRegistrar by remember {
        mutableStateOf(false)
    }
    var mostrarFecha by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = datos.fecha
    )
    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        Text(text = "Registrar movimiento financiero")

        OutlinedTextField(
            value = datos.descripcion,
            onValueChange = {
                movimientoViewModel.actualizarDescripcion(it)
            },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = datos.monto,
            onValueChange = {
                movimientoViewModel.actualizarMonto(it)
            },
            label = { Text("Monto ($)") },
            modifier = Modifier.fillMaxWidth()

        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (datos.esIngreso) "Ingreso" else "Gasto",
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = datos.esIngreso,
                onCheckedChange = {
                    movimientoViewModel.actualizarEsIngreso(it)
                }
            )
        }
        Button(
            onClick = { mostrarFecha = true }
        ) {
            Text("Seleccionar fecha")
        }

        datos.fecha?.let { fecha ->
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formato.timeZone = TimeZone.getTimeZone("UTC")

            Text(
                text = "Fecha seleccionada: ${formato.format(Date(fecha))}"
            )
        }

        if (mostrarFecha) {
            DatePickerDialog(
                onDismissRequest = { mostrarFecha = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            movimientoViewModel.actualizarFecha(
                                datePickerState.selectedDateMillis
                            )
                            mostrarFecha = false
                        }
                    ) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarFecha = false }) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = datos.confirmado,
                    onCheckedChange = {
                        movimientoViewModel.actualizarConfirmado(it)
                    }
                )

                Text(text = "Confirmo los datos del movimiento")

        }
        Button(onClick = onVolverClick) {
            Text(text = "Volver")
        }
        Button(
            onClick = {
                intentoRegistrar = true
                errores = movimientoViewModel.validarMovimiento()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Registrar movimiento")
        }

        if (intentoRegistrar) {
            if (errores.isNotEmpty()) {
                errores.values.forEach { mensaje ->
                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                Text(text = "Formulario válido. Pendiente de guardar en Room.")
            }
        }
        } //cierra column
    } //cierra pantallanuevomovimiento
