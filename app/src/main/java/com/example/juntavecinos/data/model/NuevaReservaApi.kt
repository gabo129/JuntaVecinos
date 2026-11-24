package com.example.juntavecinos.data.model

data class NuevaReservaApi(
    val usuarioId: Long,
    val dependencia: String,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val responsable: String,
    val motivo: String
)
