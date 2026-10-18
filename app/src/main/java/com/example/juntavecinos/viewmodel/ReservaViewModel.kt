package com.example.juntavecinos.viewmodel

import androidx.lifecycle.ViewModel
import com.example.juntavecinos.data.model.ReservaModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.juntavecinos.data.repository.ReservaRepository
import androidx.lifecycle.viewModelScope
import com.example.juntavecinos.data.model.ReservaEntity
import kotlinx.coroutines.launch
class ReservaViewModel(
    private val repository: ReservaRepository
) : ViewModel() {

    private val _reserva = MutableStateFlow(ReservaModel())

    val reserva: StateFlow<ReservaModel> = _reserva.asStateFlow()

    private val _mensajeGuardado = MutableStateFlow("")
    val mensajeGuardado: StateFlow<String> = _mensajeGuardado.asStateFlow()

    fun actualizarNombre(nuevoNombre: String) {
        _reserva.value = _reserva.value.copy(nombre = nuevoNombre)
    }

    fun actualizarMotivo(valor: String) {
        _reserva.value = _reserva.value.copy(motivo = valor)
    }

    fun actualizarCantidadPersonas(valor: String) {
        if (valor.all { it.isDigit() }) {
            _reserva.value = _reserva.value.copy(cantidadPersonas = valor)
        }
    }

    fun actualizarFecha(valor: Long?) {
        _reserva.value = _reserva.value.copy(fecha = valor)
    }

    fun actualizarHoraInicio(valor: String) {
        _reserva.value = _reserva.value.copy(horaInicio = valor)
    }

    fun actualizarHoraFin(valor: String) {
        _reserva.value = _reserva.value.copy(horaFin = valor)
    }

    fun actualizarAceptaCondiciones(valor: Boolean) {
        _reserva.value = _reserva.value.copy(aceptaCondiciones = valor)
    }

    fun actualizarRequiereEquipamiento(valor: Boolean) {
        _reserva.value = _reserva.value.copy(requiereEquipamiento = valor)
    }

    fun validarReserva(): Map<String, String> {

        val datos = _reserva.value
        val errores = mutableMapOf<String, String>()

        if (datos.nombre.isBlank()) {
            errores["nombre"] = "Debes ingresar un nombre"
        }

        if (datos.motivo.isBlank()) {
            errores["motivo"] = "Debes ingresar un motivo"
        }

        if ((datos.cantidadPersonas.toIntOrNull() ?: 0) <= 0) {
            errores["cantidadPersonas"] = "Ingresa una cantidad válida"
        }

        if (datos.fecha == null) {
            errores["fecha"] = "Debes seleccionar una fecha"
        }

        val formatoHora = Regex("^([01][0-9]|2[0-3]):[0-5][0-9]$")

        if (!formatoHora.matches(datos.horaInicio)) {
            errores["horaInicio"] = "Ingresa una hora válida (HH:mm)"
        }

        if (!formatoHora.matches(datos.horaFin)) {
            errores["horaFin"] = "Ingresa una hora válida (HH:mm)"
        }

        if (formatoHora.matches(datos.horaInicio) &&
            formatoHora.matches(datos.horaFin) &&
            datos.horaInicio >= datos.horaFin
        ) {

            errores["horaFin"] = "La hora de término debe ser posterior"
        }
        if (!datos.aceptaCondiciones) {
            errores["aceptaCondiciones"] = "Debes aceptar las condiciones"
        }
        return errores
    }

    fun guardarReserva() {
        if (validarReserva().isNotEmpty()) return

        _mensajeGuardado.value = ""

        val datos = _reserva.value
        val fecha = datos.fecha ?: return
        val cantidad = datos.cantidadPersonas.toIntOrNull() ?: return

        viewModelScope.launch {

            val existeReserva = repository.hayReservaSuperpuesta(
                fecha,
                datos.horaInicio,
                datos.horaFin
            )

            if (existeReserva) {
                _mensajeGuardado.value = "El horario seleccionado ya está reservado"
                return@launch
            }

            repository.guardarReserva(
                ReservaEntity(
                    nombre = datos.nombre,
                    motivo = datos.motivo,
                    cantidadPersonas = cantidad,
                    fecha = fecha,
                    horaInicio = datos.horaInicio,
                    horaFin = datos.horaFin,
                    aceptaCondiciones = datos.aceptaCondiciones,
                    requiereEquipamiento = datos.requiereEquipamiento
                )
            )

            _mensajeGuardado.value = "Reserva guardada correctamente"
            _reserva.value = ReservaModel()
        }
    }
}