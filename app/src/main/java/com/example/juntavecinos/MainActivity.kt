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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.juntavecinos.data.local.AppDatabase
import com.example.juntavecinos.data.repository.ReservaRepository
import com.example.juntavecinos.data.repository.MovimientoRepository
import com.example.juntavecinos.data.repository.UsuarioRepository
import com.example.juntavecinos.ui.screens.PantallaReservas
import com.example.juntavecinos.ui.screens.PantallaFinanzas
import com.example.juntavecinos.ui.screens.PantallaNuevaReserva
import com.example.juntavecinos.ui.screens.PantallaNuevoMovimiento
import com.example.juntavecinos.ui.screens.PantallaUbicacion
import com.example.juntavecinos.ui.theme.JuntaVecinosTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.juntavecinos.viewmodel.UsuarioViewModel
import com.example.juntavecinos.ui.screens.PantallaLogin
import com.example.juntavecinos.ui.screens.PantallaRegistro



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

        val usuarioRepository = UsuarioRepository(
            AppDatabase.getInstance(applicationContext).usuarioDao()
        )

        setContent {
            JuntaVecinosTheme {
                JuntaVecinosApp(
                    repository,
                    movimientoRepository,
                    usuarioRepository
                )
            }
        }
    }
}

@Composable
fun JuntaVecinosApp(
    repository: ReservaRepository,
    movimientoRepository: MovimientoRepository,
    usuarioRepository: UsuarioRepository
) {

    val usuarioViewModel: UsuarioViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                UsuarioViewModel(usuarioRepository)
            }
        }
    )

    val sesionIniciada by usuarioViewModel.sesionIniciada.collectAsState()

    var pantallaActual by remember {
        mutableStateOf("Login")
    }

    LaunchedEffect(sesionIniciada) {
        pantallaActual = if (sesionIniciada) "Inicio" else "Login"
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

        when (pantallaActual) {
            "Login" -> PantallaLogin(
                usuarioViewModel = usuarioViewModel,
                onRegistroClick = {
                    pantallaActual = "Registro"
                }
            )

            "Registro" -> PantallaRegistro(
                usuarioViewModel = usuarioViewModel,
                onVolverClick = {
                    pantallaActual = "Login"
                }
            )

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
                },
                onCerrarSesionClick = {
                    usuarioViewModel.cerrarSesion()
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
    onUbicacionClick: () -> Unit = {},
    onCerrarSesionClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            16.dp,
            Alignment.CenterVertically
        )
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
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
        Button(
            onClick = onCerrarSesionClick,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Cerrar sesión")
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