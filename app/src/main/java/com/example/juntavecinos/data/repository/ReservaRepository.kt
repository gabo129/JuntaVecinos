
package com.example.juntavecinos.data.repository

import com.example.juntavecinos.data.local.ReservaDao
import com.example.juntavecinos.data.model.ReservaEntity
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

    suspend fun hayReservaSuperpuesta(
        fecha: Long,
        dependencia: String,
        horaInicio: String,
        horaFin: String
    ): Boolean {
        return reservaDao.contarReservasSuperpuestas(
            fecha,
            dependencia,
            horaInicio,
            horaFin
        ) > 0
    }

    suspend fun actualizarEstado(
        id: Int,
        nuevoEstado: String
    ) {
        reservaDao.actualizarEstado(id, nuevoEstado)
    }
}
