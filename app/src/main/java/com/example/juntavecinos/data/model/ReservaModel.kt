package com.example.juntavecinos.data.model

data class ReservaModel(
    val nombre: String = "",
    val motivo: String = "",
    val cantidadPersonas: String = "",
    val fecha: Long? = null,
    val horaInicio: String = "",
    val horaFin: String = "",
    val aceptaCondiciones: Boolean = false,
    val requiereEquipamiento: Boolean = false
)