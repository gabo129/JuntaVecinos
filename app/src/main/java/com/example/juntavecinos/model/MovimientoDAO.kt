package com.example.juntavecinos.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {

    @Query("SELECT * FROM movimientos ORDER BY fecha DESC")
    fun observeAll(): Flow<List<MovimientoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movimiento: MovimientoEntity)

    @Delete
    suspend fun delete(movimiento: MovimientoEntity)
}