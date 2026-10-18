package com.example.juntavecinos.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

//entity --> tabla de room
//primary key --> identificador unico para cada reserva
@Entity(tableName = "reservas")
data class ReservaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val motivo: String,
    val cantidadPersonas: Int,
    val fecha: Long,
    val horaInicio: String,
    val horaFin: String,
    val aceptaCondiciones: Boolean,
    val requiereEquipamiento: Boolean
)