package com.example.juntavecinos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.example.juntavecinos.ui.theme.JuntaVecinosTheme
import com.example.juntavecinos.ui.screens.PantallaReservas
import com.example.juntavecinos.ui.screens.PantallaFinanzas
import com.example.juntavecinos.ui.screens.PantallaNuevaReserva
import com.example.juntavecinos.model.AppDatabase
import com.example.juntavecinos.model.ReservaRepository
import com.example.juntavecinos.ui.screens.PantallaNuevoMovimiento
import com.example.juntavecinos.model.MovimientoRepository
import com.example.juntavecinos.ui.screens.PantallaUbicacion
import androidx.compose.material3.Card

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = ReservaRepository(
            AppDatabase.getInstance(applicationContext).reservaDao()
        )
        val movimientoRepository = MovimientoRepository(
            AppDatabase.getInstance(applicationContext).movimientoDao()
        )
        setContent {
            JuntaVecinosTheme {
                JuntaVecinosApp(repository, movimientoRepository)
            }
        }
    }
}


@Composable
fun JuntaVecinosApp(
    repository: ReservaRepository,
    movimientoRepository: MovimientoRepository
) {

    var pantallaActual by remember {
        mutableStateOf("Inicio")
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

        when (pantallaActual) {

            "Inicio" -> PantallaInicio(
                modifier = Modifier.padding(innerPadding),
                onReservasClick = {
                    pantallaActual = "Reservas"
                },
                onFinanzasClick = {
                    pantallaActual = "Finanzas"
                },
                onUbicacionClick = {
                    pantallaActual = "Ubicacion"
                }
            )
            "Reservas" -> Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                PantallaReservas(
                    repository = repository,
                    onVolverClick = {
                        pantallaActual = "Inicio"
                    },
                    onNuevaReservaClick = {
                        pantallaActual = "NuevaReserva"
                    }
                )
            }
            "Finanzas" -> Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                PantallaFinanzas(
                    repository = movimientoRepository,
                    onVolverClick = {
                        pantallaActual = "Inicio"
                    },
                    onNuevoMovimientoClick = {
                        pantallaActual = "NuevoMovimiento"
                    }
                )
            }

            "Ubicacion" -> Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                PantallaUbicacion(
                    onVolverClick = {
                        pantallaActual = "Inicio"
                    }
                )
            }

            "NuevoMovimiento" -> Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                PantallaNuevoMovimiento(
                    repository = movimientoRepository,
                    onVolverClick = {
                        pantallaActual = "Finanzas"
                    }
                )

            }
            "NuevaReserva" -> Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                PantallaNuevaReserva(
                    repository = repository,
                    onVolverClick = {
                        pantallaActual = "Reservas"
                    }
                )
            }
        }
    }
}



@Composable
fun PantallaInicio(
    modifier: Modifier = Modifier,
    onReservasClick: () -> Unit = {},
    onFinanzasClick: () -> Unit = {},
    onUbicacionClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Junta de Vecinos",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "Gestión de reservas y finanzas",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Button(
            onClick = onReservasClick,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text(text = "Reservas")
        }

        Button(
            onClick = onFinanzasClick,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text(text = "Finanzas")
        }
        Button(
            onClick = onUbicacionClick,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Ubicación")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PantallaInicioPreview() {
    JuntaVecinosTheme {
        PantallaInicio()
    }
}