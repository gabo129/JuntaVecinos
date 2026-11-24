package com.example.juntavecinos.data.model

data class ReservaRemota(
    val id: Long? = null,
    val usuarioId: Long,
    val dependencia: String,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val responsable: String? = null,
    val motivo: String? = null,
    val estado: String = "Pendiente"
)
