
package com.example.juntavecinos.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservas")
data class ReservaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    @ColumnInfo(defaultValue = "''")
    val dependencia: String,
    val motivo: String,
    val cantidadPersonas: Int,
    val fecha: Long,
    val horaInicio: String,
    val horaFin: String,
    val aceptaCondiciones: Boolean,
    val requiereEquipamiento: Boolean,
    @ColumnInfo(defaultValue = "'Pendiente'")
    val estado: String = "Pendiente",
    val servidorId: Long? = null
)