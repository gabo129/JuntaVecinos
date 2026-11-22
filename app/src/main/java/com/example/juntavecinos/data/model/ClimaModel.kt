
package com.example.juntavecinos.data.model

import com.google.gson.annotations.SerializedName

data class ClimaResponse(
    val current: ClimaActual,
    val daily: ClimaDiario
)

data class ClimaActual(
    @SerializedName("temperature_2m")
    val temperatura: Double
)

data class ClimaDiario(
    @SerializedName("precipitation_probability_max")
    val probabilidadLluvia: List<Int>
)


