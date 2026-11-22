package com.example.juntavecinos.data.remote

import com.example.juntavecinos.data.model.MovimientoFinancieroApi
import retrofit2.http.GET

interface FinanzasApi {

    @GET("api/finanzas")
    suspend fun obtenerMovimientos(): List<MovimientoFinancieroApi>

}
