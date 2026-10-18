package com.example.juntavecinos.data.repository

import com.example.juntavecinos.data.local.MovimientoDao
import com.example.juntavecinos.data.model.MovimientoEntity
import kotlinx.coroutines.flow.Flow

class MovimientoRepository(
    private val movimientoDao: MovimientoDao
) {

    val movimientos: Flow<List<MovimientoEntity>> =
        movimientoDao.observeAll()

    suspend fun guardarMovimiento(movimiento: MovimientoEntity) {
        movimientoDao.insert(movimiento)
    }

    suspend fun eliminarMovimiento(movimiento: MovimientoEntity) {
        movimientoDao.delete(movimiento)
    }
}