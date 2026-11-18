
package com.example.juntavecinos.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.juntavecinos.data.model.ReservaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservaDao {

    @Query("SELECT * FROM reservas ORDER BY fecha ASC")
    fun observeAll(): Flow<List<ReservaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reserva: ReservaEntity)

    @Delete
    suspend fun delete(reserva: ReservaEntity)

    @Query("""
        SELECT COUNT(*) FROM reservas
        WHERE fecha = :fecha
        AND dependencia = :dependencia
        AND horaInicio < :horaFin
        AND horaFin > :horaInicio
        AND estado IN (
            'Pendiente',
            'Aprobada',
            'Llegada confirmada'
        )
    """)
    suspend fun contarReservasSuperpuestas(
        fecha: Long,
        dependencia: String,
        horaInicio: String,
        horaFin: String
    ): Int

    @Query("UPDATE reservas SET estado = :nuevoEstado WHERE id = :id")
    suspend fun actualizarEstado(
        id: Int,
        nuevoEstado: String
    )
}
