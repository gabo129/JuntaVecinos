package com.example.juntavecinos.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.os.CancellationSignal

fun calcularDistanciaMetros(
    latitudUsuario: Double,
    longitudUsuario: Double,
    latitudEspacio: Double,
    longitudEspacio: Double
): Float {
    val resultados = FloatArray(1)

    Location.distanceBetween(
        latitudUsuario,
        longitudUsuario,
        latitudEspacio,
        longitudEspacio,
        resultados
    )

    return resultados[0]
}

fun obtenerUbicacionActual(
    context: Context,
    onResultado: (Location?, String?) -> Unit
) {
    val permiso = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    if (permiso != PackageManager.PERMISSION_GRANTED) {
        onResultado(null, "Debes permitir la ubicación precisa")
        return
    }

    val manager = context.getSystemService(
        Context.LOCATION_SERVICE
    ) as LocationManager

    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        onResultado(null, "Debes activar el GPS")
        return
    }

    try {
        LocationManagerCompat.getCurrentLocation(
            manager,
            LocationManager.GPS_PROVIDER,
            CancellationSignal(),
            ContextCompat.getMainExecutor(context)
        ) { ubicacion ->
            if (ubicacion != null) {
                onResultado(ubicacion, null)
            } else {
                onResultado(null, "No se pudo obtener la ubicación")
            }
        }
    } catch (e: SecurityException) {
        onResultado(null, "No se concedió permiso de ubicación")
    }
}

fun puedeConfirmarLlegada(
    ubicacion: Location,
    latitudEspacio: Double,
    longitudEspacio: Double
): Boolean {
    val distancia = calcularDistanciaMetros(
        ubicacion.latitude,
        ubicacion.longitude,
        latitudEspacio,
        longitudEspacio
    )

    return ubicacion.hasAccuracy() &&
            ubicacion.accuracy <= 30f &&
            distancia <= 100f
}

fun verificarLlegada(
    context: Context,
    latitudEspacio: Double,
    longitudEspacio: Double,
    onResultado: (Boolean, String) -> Unit
) {
    obtenerUbicacionActual(context) { ubicacion, error ->
        if (ubicacion == null) {
            onResultado(false, error ?: "No se pudo obtener la ubicación")
        } else {
            val distancia = calcularDistanciaMetros(
                ubicacion.latitude,
                ubicacion.longitude,
                latitudEspacio,
                longitudEspacio
            )

            if (!ubicacion.hasAccuracy() || ubicacion.accuracy > 30f) {
                onResultado(false, "Ubicación poco precisa. Intenta nuevamente")
            } else if (puedeConfirmarLlegada(
                    ubicacion,
                    latitudEspacio,
                    longitudEspacio
                )
            ) {
                onResultado(
                    true,
                    "Llegada permitida. Estás a ${distancia.toInt()} metros"
                )
            } else {
                onResultado(
                    false,
                    "Llegada rechazada. Estás a ${distancia.toInt()} metros"
                )
            }
        }
    }
}
