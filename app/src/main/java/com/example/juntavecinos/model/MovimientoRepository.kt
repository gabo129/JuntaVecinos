package com.example.juntavecinos.model

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