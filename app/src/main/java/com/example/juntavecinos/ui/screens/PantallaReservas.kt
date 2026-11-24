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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.juntavecinos.data.model.EstadoReserva
import com.example.juntavecinos.data.model.ReservaRemota
import com.example.juntavecinos.data.model.UbicacionLlegada
import com.example.juntavecinos.data.remote.AdminRetrofitClient
import com.example.juntavecinos.data.remote.ReservasRetrofitClient
import com.example.juntavecinos.data.repository.ReservaRepository
import com.example.juntavecinos.utils.obtenerUbicacionActual
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun PantallaReservas(
    repository: ReservaRepository,
    onVolverClick: () -> Unit,
    onNuevaReservaClick: () -> Unit = {},
    autorizacion: String? = null
) {
    val reservasLocales by repository.reservas.collectAsState(
        initial = emptyList()
    )

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var reservasRemotas by remember {
        mutableStateOf<List<ReservaRemota>>(emptyList())
    }

    var rol by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    var actualizacion by remember { mutableIntStateOf(0) }
    var procesandoId by remember { mutableStateOf<Long?>(null) }

    var reservaParaGps by remember {
        mutableStateOf<ReservaRemota?>(null)
    }

    LaunchedEffect(autorizacion, actualizacion) {
        val token = autorizacion

        if (token == null) {
            mensaje = "Debes iniciar sesión"
            reservasRemotas = emptyList()
            rol = ""
            return@LaunchedEffect
        }

        cargando = true

        try {
            val usuario = AdminRetrofitClient.adminApi
                .obtenerUsuario(token)

            rol = usuario.rol

            val recibidas = ReservasRetrofitClient.reservaApi
                .listarReservas(token)

            reservasRemotas = recibidas

            recibidas.forEach { remota ->
                val idServidor = remota.id

                if (idServidor != null) {
                    val local = reservasLocales.firstOrNull {
                        it.servidorId == idServidor
                    }

                    if (
                        local != null &&
                        local.estado != remota.estado
                    ) {
                        repository.actualizarEstado(
                            local.id,
                            remota.estado
                        )
                    }
                }
            }

            mensaje = ""

        } catch (e: HttpException) {
            reservasRemotas = emptyList()
            rol = ""

            mensaje = when (e.code()) {
                401 -> "Debes iniciar sesión nuevamente"
                403 -> "No tienes permiso para ver las reservas"
                else -> "Error del servidor: ${e.code()}"
            }

        } catch (e: Exception) {
            mensaje = "No se pudo conectar con MySQL"
            reservasRemotas = emptyList()
            rol = ""

        } finally {
            cargando = false
        }
    }

    fun cambiarEstado(
        reserva: ReservaRemota,
        nuevoEstado: String
    ) {
        val token = autorizacion ?: return
        val idServidor = reserva.id ?: return

        if (procesandoId != null) return

        scope.launch {
            procesandoId = idServidor
            mensaje = ""

            try {
                val actualizada = ReservasRetrofitClient.reservaApi
                    .actualizarEstado(
                        token,
                        idServidor,
                        EstadoReserva(nuevoEstado)
                    )

                reservasRemotas = reservasRemotas.map {
                    if (it.id == idServidor) {
                        actualizada
                    } else {
                        it
                    }
                }

                val local = reservasLocales.firstOrNull {
                    it.servidorId == idServidor
                }

                if (local != null) {
                    repository.actualizarEstado(
                        local.id,
                        actualizada.estado
                    )
                }

                mensaje = "Reserva actualizada correctamente"

            } catch (e: HttpException) {
                mensaje = when (e.code()) {
                    400 -> "El cambio de estado no está permitido"
                    401 -> "Debes iniciar sesión nuevamente"
                    403 -> "No tienes permiso para realizar esta acción"
                    404 -> "La reserva ya no existe"
                    else -> "Error del servidor: ${e.code()}"
                }

            } catch (e: Exception) {
                mensaje = "No se pudo completar la operación"

            } finally {
                procesandoId = null
            }
        }
    }

    fun eliminarReserva(remota: ReservaRemota) {
        val token = autorizacion ?: return
        val idServidor = remota.id ?: return

        if (procesandoId != null) return

        scope.launch {
            procesandoId = idServidor
            mensaje = ""

            try {
                ReservasRetrofitClient.reservaApi
                    .eliminarReserva(token, idServidor)

                reservasRemotas = reservasRemotas.filter {
                    it.id != idServidor
                }

                val local = reservasLocales.firstOrNull {
                    it.servidorId == idServidor
                }

                if (local != null) {
                    repository.eliminarReserva(local)
                }

                mensaje = "Reserva eliminada correctamente"

            } catch (e: HttpException) {
                mensaje = when (e.code()) {
                    403 -> "Solo el administrador puede eliminar"
                    404 -> "La reserva ya no existe"
                    else -> "Error del servidor: ${e.code()}"
                }

            } catch (e: Exception) {
                mensaje = "No se pudo completar la eliminación"

            } finally {
                procesandoId = null
            }
        }
    }

    fun confirmarLlegadaGps(reserva: ReservaRemota) {
        val token = autorizacion ?: return
        val idServidor = reserva.id ?: return

        if (procesandoId != null) return

        mensaje = "Obteniendo ubicación..."

        obtenerUbicacionActual(context) { ubicacion, error ->
            if (ubicacion == null) {
                mensaje = error ?: "No se pudo obtener el GPS"

            } else if (
                !ubicacion.hasAccuracy() ||
                ubicacion.accuracy > 30f
            ) {
                mensaje = "La ubicación no tiene precisión suficiente"

            } else {
                scope.launch {
                    procesandoId = idServidor

                    try {
                        val respuesta = ReservasRetrofitClient.reservaApi
                            .confirmarLlegada(
                                token,
                                idServidor,
                                UbicacionLlegada(
                                    latitud = ubicacion.latitude,
                                    longitud = ubicacion.longitude,
                                    precision = ubicacion.accuracy.toDouble()
                                )
                            )

                        reservasRemotas = reservasRemotas.map {
                            if (it.id == idServidor) {
                                respuesta
                            } else {
                                it
                            }
                        }

                        val local = reservasLocales.firstOrNull {
                            it.servidorId == idServidor
                        }

                        if (local != null) {
                            repository.actualizarEstado(
                                local.id,
                                respuesta.estado
                            )
                        }

                        mensaje = "Llegada confirmada correctamente"

                    } catch (e: HttpException) {
                        mensaje = when (e.code()) {
                            400 -> "El servidor rechazó la llegada. Revisa la fecha, horario y ubicación"
                            403 -> "No tienes permiso para confirmar esta reserva"
                            404 -> "La reserva no existe"
                            else -> "Error del servidor: ${e.code()}"
                        }

                    } catch (e: Exception) {
                        mensaje = "No se pudo confirmar la llegada"

                    } finally {
                        procesandoId = null
                    }
                }
            }
        }
    }

    val permisoGps = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        val pendiente = reservaParaGps

        if (concedido && pendiente != null) {
            confirmarLlegadaGps(pendiente)
        } else {
            mensaje = "Debes permitir el acceso a la ubicación precisa"
        }

        reservaParaGps = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Reservas",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(onClick = onNuevaReservaClick) {
            Text("Nueva Reserva")
        }

        Text(
            text = "Reservas desde MySQL",
            style = MaterialTheme.typography.titleLarge
        )

        Button(
            onClick = { actualizacion++ },
            enabled = !cargando && procesandoId == null
        ) {
            Text("Actualizar")
        }

        if (cargando) {
            CircularProgressIndicator()
        }

        if (mensaje.isNotEmpty()) {
            Text(mensaje)
        }

        if (!cargando && reservasRemotas.isEmpty()) {
            Text("No hay reservas disponibles en MySQL")
        }

        reservasRemotas.forEach { reserva ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = reserva.dependencia,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text("Fecha: ${reserva.fecha}")

                    Text(
                        "Horario: ${reserva.horaInicio} a ${reserva.horaFin}"
                    )

                    Text(
                        "Responsable: ${
                            reserva.responsable ?: "Sin especificar"
                        }"
                    )

                    Text(
                        "Motivo: ${
                            reserva.motivo ?: "Sin especificar"
                        }"
                    )

                    Text("Estado: ${reserva.estado}")

                    val disponible = procesandoId == null

                    if (rol == "ADMIN" && reserva.estado == "Pendiente") {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                enabled = disponible,
                                onClick = {
                                    cambiarEstado(reserva, "Aprobada")
                                }
                            ) {
                                Text("Aprobar")
                            }

                            Button(
                                enabled = disponible,
                                onClick = {
                                    cambiarEstado(reserva, "Rechazada")
                                }
                            ) {
                                Text("Rechazar")
                            }
                        }
                    }

                    if (
                        rol == "ADMIN" &&
                        reserva.estado in listOf("Pendiente", "Aprobada")
                    ) {
                        Button(
                            enabled = disponible,
                            onClick = {
                                cambiarEstado(reserva, "Cancelada")
                            }
                        ) {
                            Text("Cancelar reserva")
                        }
                    }

                    if (reserva.estado == "Aprobada") {
                        Button(
                            enabled = disponible,
                            onClick = {
                                val permiso = ContextCompat
                                    .checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    )

                                if (
                                    permiso == PackageManager.PERMISSION_GRANTED
                                ) {
                                    confirmarLlegadaGps(reserva)
                                } else {
                                    reservaParaGps = reserva

                                    permisoGps.launch(
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    )
                                }
                            }
                        ) {
                            Text("Confirmar llegada con GPS")
                        }
                    }

                    if (rol == "ADMIN") {
                        Button(
                            enabled = disponible,
                            onClick = {
                                eliminarReserva(reserva)
                            }
                        ) {
                            Text("Eliminar")
                        }
                    }
                }
            }
        }

        Text(
            text = "Reservas guardadas en este dispositivo",
            style = MaterialTheme.typography.titleLarge
        )

        if (reservasLocales.isEmpty()) {
            Text("No hay reservas guardadas en Room")
        } else {
            reservasLocales.forEach { reserva ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = reserva.motivo,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text("Responsable: ${reserva.nombre}")
                        Text("Dependencia: ${reserva.dependencia}")

                        val formato = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        )

                        formato.timeZone = TimeZone.getTimeZone("UTC")

                        Text(
                            "Fecha: ${
                                formato.format(Date(reserva.fecha))
                            }"
                        )

                        Text(
                            "Horario: ${reserva.horaInicio} a ${reserva.horaFin}"
                        )

                        Text("Estado local: ${reserva.estado}")

                        if (reserva.servidorId != null) {
                            Text(
                                "Sincronizada con MySQL: ID ${
                                    reserva.servidorId
                                }"
                            )
                        } else {
                            Text("Reserva antigua guardada solo en Room")
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
