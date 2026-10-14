package com.example.juntavecinos.model
import kotlinx.coroutines.flow.Flow


class ReservaRepository(
    private val reservaDao: ReservaDao
) {

    val reservas: Flow<List<ReservaEntity>> =
        reservaDao.observeAll()

    suspend fun guardarReserva(reserva: ReservaEntity) {
        reservaDao.insert(reserva)
    }

    suspend fun eliminarReserva(reserva: ReservaEntity) {
        reservaDao.delete(reserva)
    }
}