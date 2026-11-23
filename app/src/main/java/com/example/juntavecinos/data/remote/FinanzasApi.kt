package com.example.juntavecinos.data.remote

import com.example.juntavecinos.data.model.MovimientoFinancieroApi
import com.example.juntavecinos.data.model.NuevoMovimientoApi
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FinanzasApi {

    @GET("api/finanzas")
    suspend fun obtenerMovimientos(): List<MovimientoFinancieroApi>

    @POST("api/finanzas")
    suspend fun guardarMovimiento(
        @Body movimiento: NuevoMovimientoApi
    ): MovimientoFinancieroApi

    @DELETE("api/finanzas/{id}")
    suspend fun eliminarMovimiento(
        @Path("id") id: Long
    ): Response<Unit>
}
