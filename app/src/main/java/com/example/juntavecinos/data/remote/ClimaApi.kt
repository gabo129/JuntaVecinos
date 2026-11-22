
package com.example.juntavecinos.data.remote

import com.example.juntavecinos.data.model.ClimaResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ClimaApi {

    @GET("v1/forecast")
    suspend fun obtenerClima(
        @Query("latitude") latitud: Double = -33.4489,
        @Query("longitude") longitud: Double = -70.6693,
        @Query("current") actual: String = "temperature_2m",
        @Query("daily") diario: String = "precipitation_probability_max",
        @Query("timezone") zonaHoraria: String = "America/Santiago"
    ): ClimaResponse
}

