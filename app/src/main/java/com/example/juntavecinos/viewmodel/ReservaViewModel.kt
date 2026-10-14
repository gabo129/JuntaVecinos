package com.example.juntavecinos.viewmodel

import androidx.lifecycle.ViewModel
import com.example.juntavecinos.model.ReservaModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReservaViewModel : ViewModel() {

    private val _reserva = MutableStateFlow(ReservaModel())

    val reserva: StateFlow<ReservaModel> = _reserva.asStateFlow()

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

        if (datos.horaInicio.isBlank()) {
            errores["horaInicio"] = "Ingresa la hora de inicio"
        }

        if (datos.horaFin.isBlank()) {
            errores["horaFin"] = "Ingresa la hora de término"
        }

        if (datos.horaInicio.isNotBlank() &&
            datos.horaFin.isNotBlank() &&
            datos.horaInicio >= datos.horaFin) {
            errores["horaFin"] = "La hora de término debe ser posterior"
        }

        if (!datos.aceptaCondiciones) {
            errores["aceptaCondiciones"] = "Debes aceptar las condiciones"
        }

        return errores
    }
}