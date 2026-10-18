package com.example.juntavecinos.viewmodel

import androidx.lifecycle.ViewModel
import com.example.juntavecinos.data.model.MovimientoModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.juntavecinos.data.repository.MovimientoRepository
import androidx.lifecycle.viewModelScope
import com.example.juntavecinos.data.model.MovimientoEntity
import kotlinx.coroutines.launch
class MovimientoViewModel(
    private val repository: MovimientoRepository
) : ViewModel() {

    private val _movimiento = MutableStateFlow(MovimientoModel())

    val movimiento: StateFlow<MovimientoModel> =
        _movimiento.asStateFlow()

    private val _mensajeGuardado = MutableStateFlow("")

    val mensajeGuardado: StateFlow<String> =
        _mensajeGuardado.asStateFlow()

    fun actualizarDescripcion(valor: String) {
        _movimiento.value = _movimiento.value.copy(descripcion = valor)
    }

    fun actualizarMonto(valor: String) {
        if (valor.all { it.isDigit() }) {
            _movimiento.value = _movimiento.value.copy(monto = valor)
        }
    }

    fun actualizarFecha(valor: Long?) {
        _movimiento.value = _movimiento.value.copy(fecha = valor)
    }

    fun actualizarEsIngreso(valor: Boolean) {
        _movimiento.value = _movimiento.value.copy(esIngreso = valor)
    }

    fun actualizarConfirmado(valor: Boolean) {
        _movimiento.value = _movimiento.value.copy(confirmado = valor)
    }
    fun validarMovimiento(): Map<String, String> {

        val datos = _movimiento.value
        val errores = mutableMapOf<String, String>()

        if (datos.descripcion.isBlank()) {
            errores["descripcion"] = "Debes ingresar una descripción"
        }

        if ((datos.monto.toLongOrNull() ?: 0L) <= 0L) {
            errores["monto"] = "Debes ingresar un monto válido"
        }

        if (datos.fecha == null) {
            errores["fecha"] = "Debes seleccionar una fecha"
        }

        if (!datos.confirmado) {
            errores["confirmado"] = "Debes confirmar los datos"
        }

        return errores
    }
    fun guardarMovimiento() {
        if (validarMovimiento().isNotEmpty()) return

        val datos = _movimiento.value
        val fecha = datos.fecha ?: return
        val monto = datos.monto.toLongOrNull() ?: return

        _mensajeGuardado.value = ""

        viewModelScope.launch {
            repository.guardarMovimiento(
                MovimientoEntity(
                    descripcion = datos.descripcion,
                    monto = monto,
                    fecha = fecha,
                    esIngreso = datos.esIngreso,
                    confirmado = datos.confirmado
                )
            )

            _movimiento.value = MovimientoModel()
            _mensajeGuardado.value = "Movimiento guardado correctamente"
        }
    }
}