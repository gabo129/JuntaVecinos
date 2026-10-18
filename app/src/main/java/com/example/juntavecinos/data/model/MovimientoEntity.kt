package com.example.juntavecinos.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movimientos")
data class MovimientoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val descripcion: String,
    val monto: Long,
    val fecha: Long,
    val esIngreso: Boolean,
    val confirmado: Boolean
)