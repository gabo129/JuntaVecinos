
package com.example.juntavecinos.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.juntavecinos.data.repository.ReservaRepository
import com.example.juntavecinos.utils.verificarLlegada
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
    val context = LocalContext.current

    var mensajeGPS by remember { mutableStateOf("") }
    var reservaMensajeId by remember { mutableStateOf<Int?>(null) }
    var reservaPendienteId by remember { mutableStateOf<Int?>(null) }
    var verificandoGPS by remember { mutableStateOf(false) }

    fun confirmarLlegadaGPS(id: Int) {
        reservaMensajeId = id
        mensajeGPS = "Verificando ubicación..."
        verificandoGPS = true

        verificarLlegada(
            context = context,
            latitudEspacio = -33.4372,
            longitudEspacio = -70.6506
        ) { permitida, mensaje ->

            if (permitida) {
                scope.launch {
                    try {
                        repository.actualizarEstado(
                            id,
                            "Llegada confirmada"
                        )
                        mensajeGPS = "Llegada confirmada. $mensaje"
                    } catch (e: Exception) {
                        mensajeGPS = "No se pudo guardar la llegada"
                    } finally {
                        verificandoGPS = false
                    }
                }
            } else {
                mensajeGPS = mensaje
                verificandoGPS = false
            }
        }
    }

    val permisoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->

        val id = reservaPendienteId

        if (concedido && id != null) {
            confirmarLlegadaGPS(id)
        } else {
            mensajeGPS = "Debes permitir la ubicación precisa"
            verificandoGPS = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Reservas",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(onClick = onNuevaReservaClick) {
            Text("Nueva Reserva")
        }

        if (reservas.isEmpty()) {
            Text("No hay reservas registradas")
        } else {
            reservas.forEach { reserva ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = reserva.motivo,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text("Responsable: ${reserva.nombre}")

                        Text(
                            "Dependencia: ${
                                reserva.dependencia.ifBlank {
                                    "Sin especificar"
                                }
                            }"
                        )

                        val formatoFecha = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        )

                        formatoFecha.timeZone =
                            TimeZone.getTimeZone("UTC")

                        Text(
                            "Fecha: ${
                                formatoFecha.format(Date(reserva.fecha))
                            }"
                        )

                        Text(
                            "Horario: ${reserva.horaInicio} a ${reserva.horaFin}"
                        )

                        Text(
                            text = "Estado: ${reserva.estado}",
                            color = when (reserva.estado) {
                                "Aprobada",
                                "Llegada confirmada" ->
                                    MaterialTheme.colorScheme.primary

                                "Rechazada",
                                "Cancelada" ->
                                    MaterialTheme.colorScheme.error

                                else ->
                                    MaterialTheme.colorScheme.onSurface
                            }
                        )

                        if (reserva.estado == "Pendiente") {
                            Row(
                                horizontalArrangement =
                                    Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            repository.actualizarEstado(
                                                reserva.id,
                                                "Aprobada"
                                            )
                                        }
                                    }
                                ) {
                                    Text("Aprobar")
                                }

                                Button(
                                    onClick = {
                                        scope.launch {
                                            repository.actualizarEstado(
                                                reserva.id,
                                                "Rechazada"
                                            )
                                        }
                                    }
                                ) {
                                    Text("Rechazar")
                                }
                            }
                        }

                        if (reserva.estado == "Aprobada") {
                            Button(
                                enabled = !verificandoGPS,
                                onClick = {
                                    reservaPendienteId = reserva.id
                                    reservaMensajeId = reserva.id

                                    val permiso = ContextCompat
                                        .checkSelfPermission(
                                            context,
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                        )

                                    if (permiso ==
                                        PackageManager.PERMISSION_GRANTED
                                    ) {
                                        confirmarLlegadaGPS(reserva.id)
                                    } else {
                                        permisoLauncher.launch(
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                        )
                                    }
                                }
                            ) {
                                Text("Confirmar llegada con GPS")
                            }
                        }

                        if (reservaMensajeId == reserva.id &&
                            mensajeGPS.isNotEmpty()
                        ) {
                            Text(mensajeGPS)
                        }

                        if (reserva.estado == "Llegada confirmada") {
                            Text("Asistencia registrada mediante GPS")
                        }

                        if (
                            reserva.estado == "Pendiente" ||
                            reserva.estado == "Aprobada"
                        ) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        repository.actualizarEstado(
                                            reserva.id,
                                            "Cancelada"
                                        )
                                    }
                                }
                            ) {
                                Text("Cancelar reserva")
                            }
                        }

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
            Text("Volver")
        }
    }
}
