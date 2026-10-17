package com.example.juntavecinos.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.os.CancellationSignal

@SuppressLint("MissingPermission")
@Composable
fun PantallaUbicacion(
    onVolverClick: () -> Unit
) {
    val context = LocalContext.current

    var mensaje by remember {
        mutableStateOf("Presiona el botón para consultar tu ubicación")
    }

    val obtenerUbicacion: () -> Unit = {

        val permisoPreciso = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val permisoAproximado = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!permisoPreciso && !permisoAproximado) {
            mensaje = "No se concedió permiso de ubicación"
        } else {

            val manager = context.getSystemService(
                Context.LOCATION_SERVICE
            ) as LocationManager

            val proveedor = when {
                permisoPreciso && manager.isProviderEnabled(
                    LocationManager.GPS_PROVIDER
                ) -> LocationManager.GPS_PROVIDER

                manager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
                ) -> LocationManager.NETWORK_PROVIDER

                else -> null
            }

            if (proveedor == null) {
                mensaje = "Activa la ubicación del dispositivo"
            } else {
                mensaje = "Buscando ubicación..."

                try {
                    LocationManagerCompat.getCurrentLocation(
                        manager,
                        proveedor,
                        CancellationSignal(),
                        ContextCompat.getMainExecutor(context)
                    ) { ubicacion ->

                        mensaje = if (ubicacion != null) {
                            "Latitud: ${ubicacion.latitude}\nLongitud: ${ubicacion.longitude}"
                        } else {
                            "No se pudo obtener la ubicación"
                        }
                    }
                } catch (e: SecurityException) {
                    mensaje = "No se concedió permiso de ubicación"
                }
            }
        }
    }

    val permisoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->

        val concedido =
            permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (concedido) {
            obtenerUbicacion()
        } else {
            mensaje = "Permiso de ubicación denegado"
        }
    }

    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        Text(text = "Ubicación del dispositivo")

        Button(
            onClick = {
                val permisoPreciso = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                val permisoAproximado = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (permisoPreciso || permisoAproximado) {
                    obtenerUbicacion()
                } else {
                    permisoLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
        ) {
            Text(text = "Consultar mi ubicación")
        }

        Text(text = mensaje)

        Button(
            onClick = onVolverClick
        ) {
            Text(text = "Volver")
        }
    }
}

