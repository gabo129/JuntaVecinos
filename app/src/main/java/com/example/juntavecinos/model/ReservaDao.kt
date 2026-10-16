package com.example.juntavecinos.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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
    AND horaInicio < :horaFin
    AND horaFin > :horaInicio
""")
    suspend fun contarReservasSuperpuestas(
        fecha: Long,
        horaInicio: String,
        horaFin: String
    ): Int
}