package com.example.juntavecinos.data.model

data class MovimientoModel(
    val descripcion: String = "",
    val monto: String = "",
    val fecha: Long? = null,
    val esIngreso: Boolean = true,
    val confirmado: Boolean = false
)