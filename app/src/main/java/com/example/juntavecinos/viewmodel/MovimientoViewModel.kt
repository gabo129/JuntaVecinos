package com.example.juntavecinos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juntavecinos.data.model.MovimientoModel
import com.example.juntavecinos.data.model.MovimientoEntity
import com.example.juntavecinos.data.model.NuevoMovimientoApi
import com.example.juntavecinos.data.remote.FinanzasRetrofitClient
import com.example.juntavecinos.data.repository.MovimientoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MovimientoViewModel(
    private val repository: MovimientoRepository
) : ViewModel() {

    private val _movimiento = MutableStateFlow(MovimientoModel())

    val movimiento: StateFlow<MovimientoModel> =
        _movimiento.asStateFlow()

    private val _mensajeGuardado = MutableStateFlow("")

    val mensajeGuardado: StateFlow<String> =
        _mensajeGuardado.asStateFlow()

    private var guardando = false

    fun actualizarDescripcion(valor: String) {
        _movimiento.value = _movimiento.value.copy(descripcion = valor)
    }

    fun actualizarMonto(valor: String) {
        if (valor.all { it.isDigit() }) {
            _movimiento.value = _movimiento.value.copy(monto = valor)
        }
    }

    fun actualizarCategoria(valor: String) {
        _movimiento.value = _movimiento.value.copy(categoria = valor)
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

        if (guardando || validarMovimiento().isNotEmpty()) return

        val datos = _movimiento.value
        val fecha = datos.fecha ?: return
        val monto = datos.monto.toLongOrNull() ?: return

        val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        formatoFecha.timeZone = TimeZone.getTimeZone("UTC")

        val nuevoMovimiento = NuevoMovimientoApi(
            tipo = if (datos.esIngreso) "INGRESO" else "GASTO",
            categoria = datos.categoria,
            descripcion = datos.descripcion,
            monto = monto,
            fecha = formatoFecha.format(Date(fecha))
        )

        _mensajeGuardado.value = ""
        guardando = true

        viewModelScope.launch {

            try {
                val movimientoServidor =
                    FinanzasRetrofitClient.finanzasApi.guardarMovimiento(
                        nuevoMovimiento
                    )

                try {
                    repository.guardarMovimiento(
                        MovimientoEntity(
                            descripcion = datos.descripcion,
                            monto = monto,
                            fecha = fecha,
                            esIngreso = datos.esIngreso,
                            confirmado = datos.confirmado,
                            categoria = datos.categoria,
                            servidorId = movimientoServidor.id
                        )
                    )

                    _mensajeGuardado.value = "Movimiento guardado correctamente"

                } catch (e: Exception) {
                    _mensajeGuardado.value =
                        "Guardado en MySQL, pero no se pudo guardar en Room"
                }

                _movimiento.value = MovimientoModel()

            } catch (e: Exception) {
                _mensajeGuardado.value =
                    "No se pudo confirmar el registro en MySQL"
            } finally {
                guardando = false
            }
        }
    }
}
