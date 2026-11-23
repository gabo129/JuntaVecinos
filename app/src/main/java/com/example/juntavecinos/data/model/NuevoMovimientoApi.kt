package com.example.juntavecinos.data.model

data class NuevoMovimientoApi(
    val tipo: String,
    val categoria: String,
    val descripcion: String,
    val monto: Long,
    val fecha: String
)
