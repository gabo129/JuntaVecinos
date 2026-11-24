package com.example.juntavecinos.data.remote

import com.example.juntavecinos.data.model.EstadoReserva
import com.example.juntavecinos.data.model.NuevaReservaApi
import com.example.juntavecinos.data.model.ReservaRemota
import com.example.juntavecinos.data.model.UbicacionLlegada
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReservaApi {

    @GET("api/reservas")
    suspend fun listarReservas(
        @Header("Authorization") autorizacion: String
    ): List<ReservaRemota>

    @POST("api/reservas")
    suspend fun crearReserva(
        @Header("Authorization") autorizacion: String,
        @Body reserva: NuevaReservaApi
    ): ReservaRemota

    @PUT("api/reservas/{id}/estado")
    suspend fun actualizarEstado(
        @Header("Authorization") autorizacion: String,
        @Path("id") id: Long,
        @Body datos: EstadoReserva
    ): ReservaRemota

    @DELETE("api/reservas/{id}")
    suspend fun eliminarReserva(
        @Header("Authorization") autorizacion: String,
        @Path("id") id: Long
    )

    @PUT("api/reservas/{id}/llegada")
    suspend fun confirmarLlegada(
        @Header("Authorization") autorizacion: String,
        @Path("id") id: Long,
        @Body ubicacion: UbicacionLlegada
    ): ReservaRemota
}
