package com.example.juntavecinos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.juntavecinos.viewmodel.ReservaViewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Switch
import androidx.compose.ui.Alignment
import androidx.compose.material3.Button
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
import com.example.juntavecinos.data.repository.ReservaRepository
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaNuevaReserva(
    repository: ReservaRepository,
    reservaViewModel: ReservaViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                ReservaViewModel(repository)
            }
        }
    ),
    onVolverClick: () -> Unit = {}
) {

    val datos by reservaViewModel.reserva.collectAsState()
    val mensajeGuardado by reservaViewModel.mensajeGuardado.collectAsState()
    var mostrarFecha by remember { mutableStateOf(false) }
    var errores by remember {
        mutableStateOf<Map<String, String>>(emptyMap())
    }

    var intentoGuardar by remember {
        mutableStateOf(false)
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = datos.fecha

    )
    val horasDisponibles = (0 until 48).map { indice ->
        "%02d:%02d".format(indice / 2, (indice % 2) * 30)
    }

    var menuInicioAbierto by remember {
        mutableStateOf(false)
    }
    var menuFinAbierto by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Text(
            text = "Nueva Reserva",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = datos.nombre,
            onValueChange = { reservaViewModel.actualizarNombre(it) },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            isError = errores.containsKey("nombre"),
            leadingIcon ={
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_edit),
                    contentDescription = "Nombre del responsable"
                )
            },

        )

        if (errores.containsKey("nombre")) {
            Text(
                text = errores["nombre"] ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }

        OutlinedTextField(
            value = datos.motivo,
            onValueChange = { reservaViewModel.actualizarMotivo(it) },
            label = { Text("Motivo de reserva") },
            modifier = Modifier.fillMaxWidth(),
            isError = errores.containsKey("motivo")
        )

        if (errores.containsKey("motivo")) {
            Text(
                text = errores["motivo"] ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }

        OutlinedTextField(
            value = datos.cantidadPersonas,
            onValueChange = { reservaViewModel.actualizarCantidadPersonas(it) },
            label = { Text("Cantidad de personas") },
            modifier = Modifier.fillMaxWidth(),
            isError = errores.containsKey("cantidadPersonas")
        )

        if (errores.containsKey("cantidadPersonas")) {
            Text(
                text = errores["cantidadPersonas"] ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }

        ExposedDropdownMenuBox(
            expanded = menuInicioAbierto,
            onExpandedChange = {
                menuInicioAbierto = it
            }
        ) {
            OutlinedTextField(
                value = datos.horaInicio,
                onValueChange = {},
                readOnly = true,
                label = { Text("Hora de inicio") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                isError = errores.containsKey("horaInicio"),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = menuInicioAbierto
                    )
                }
            )

            ExposedDropdownMenu(
                expanded = menuInicioAbierto,
                onDismissRequest = {
                    menuInicioAbierto = false
                }
            ) {
                horasDisponibles.forEach { hora ->
                    DropdownMenuItem(
                        text = { Text(hora) },
                        onClick = {
                            reservaViewModel.actualizarHoraInicio(hora)
                            menuInicioAbierto = false
                        }
                    )
                }
            }
        }

        if (errores.containsKey("horaInicio")) {
            Text(
                text = errores["horaInicio"] ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }

        ExposedDropdownMenuBox(
            expanded = menuFinAbierto,
            onExpandedChange = {
                menuFinAbierto = it
            }
        ) {
            OutlinedTextField(
                value = datos.horaFin,
                onValueChange = {},
                readOnly = true,
                label = { Text("Hora de término") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                isError = errores.containsKey("horaFin"),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = menuFinAbierto
                    )
                }
            )

            ExposedDropdownMenu(
                expanded = menuFinAbierto,
                onDismissRequest = {
                    menuFinAbierto = false
                }
            ) {
                horasDisponibles.forEach { hora ->
                    DropdownMenuItem(
                        text = { Text(hora) },
                        onClick = {
                            reservaViewModel.actualizarHoraFin(hora)
                            menuFinAbierto = false
                        }
                    )
                }
            }
        }

        if (errores.containsKey("horaFin")) {
            Text(
                text = errores["horaFin"] ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = datos.aceptaCondiciones,
                onCheckedChange = {
                    reservaViewModel.actualizarAceptaCondiciones(it)
                }
            )

            Text(text = "Acepto las condiciones de reserva")
        }
        if (errores.containsKey("aceptaCondiciones")) {
            Text(
                text = errores["aceptaCondiciones"] ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "¿Requiere equipamiento?")

            Switch(
                checked = datos.requiereEquipamiento,
                onCheckedChange = {
                    reservaViewModel.actualizarRequiereEquipamiento(it)
                }
            )
        }
        Button(
            onClick = { mostrarFecha = true }
        ) {
            Text("Seleccionar fecha")
        }

        if (errores.containsKey("fecha")) {
            Text(
                text = errores["fecha"] ?: "",
                color = MaterialTheme.colorScheme.error
            )
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
                            reservaViewModel.actualizarFecha(
                                datePickerState.selectedDateMillis
                            )
                            mostrarFecha = false
                        }
                    ) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { mostrarFecha = false }
                    ) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
        Button(
            onClick = {
                intentoGuardar = true
                errores = reservaViewModel.validarReserva()

                if (errores.isEmpty()) {
                    reservaViewModel.guardarReserva()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Guardar reserva")
        }

        AnimatedVisibility(
            visible = intentoGuardar &&
                    errores.isEmpty() &&
                    mensajeGuardado.isNotEmpty(),
            enter = fadeIn() + expandVertically()
        ) {
            Text(
                text = mensajeGuardado,
                color = if (mensajeGuardado == "El horario seleccionado ya está reservado") {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        }
        Button(
            onClick = onVolverClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Volver")
        }
    }
}