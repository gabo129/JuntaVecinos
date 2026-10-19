package com.example.juntavecinos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juntavecinos.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsuarioViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {

    private val _mensaje = MutableStateFlow("")
    val mensaje = _mensaje.asStateFlow()

    private val _sesionIniciada = MutableStateFlow(false)
    val sesionIniciada = _sesionIniciada.asStateFlow()

    fun registrar(nombre: String, clave: String) {
        if (nombre.isBlank() || clave.isBlank()) {
            _mensaje.value = "Completa todos los campos"
            return
        }

        viewModelScope.launch {
            val registrado = repository.registrarUsuario(nombre.trim(), clave)

            _mensaje.value = if (registrado) {
                "Usuario registrado correctamente"
            } else {
                "Ese nombre de usuario ya existe"
            }
        }
    }

    fun iniciarSesion(nombre: String, clave: String) {
        if (nombre.isBlank() || clave.isBlank()) {
            _mensaje.value = "Completa todos los campos"
            return
        }

        viewModelScope.launch {
            val correcto = repository.iniciarSesion(nombre.trim(), clave)

            _sesionIniciada.value = correcto

            _mensaje.value = if (correcto) {
                "Inicio de sesión correcto"
            } else {
                "Usuario o contraseña incorrectos"
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = ""
    }

    fun cerrarSesion() {
        _sesionIniciada.value = false
        _mensaje.value = ""
    }
}