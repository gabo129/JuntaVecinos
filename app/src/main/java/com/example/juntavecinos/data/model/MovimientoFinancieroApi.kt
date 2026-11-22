package com.example.juntavecinos.data.model

import java.math.BigDecimal

data class MovimientoFinancieroApi(
    val id: Long,
    val tipo: String,
    val categoria: String,
    val descripcion: String,
    val monto: BigDecimal,
    val fecha: String
)
