package com.example.juntavecinos.viewmodel

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juntavecinos.data.model.RegistroUsuario
import com.example.juntavecinos.data.model.UsuarioServidor
import com.example.juntavecinos.data.remote.AdminRetrofitClient
import com.example.juntavecinos.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class UsuarioViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {

    private val _mensaje = MutableStateFlow("")
    val mensaje = _mensaje.asStateFlow()

    private val _sesionIniciada = MutableStateFlow(false)
    val sesionIniciada = _sesionIniciada.asStateFlow()

    private val _usuarioActual = MutableStateFlow<UsuarioServidor?>(null)
    val usuarioActual = _usuarioActual.asStateFlow()

    private val _autorizacion = MutableStateFlow<String?>(null)
    val autorizacion = _autorizacion.asStateFlow()

    fun registrar(nombre: String, correo: String, clave: String) {
        if (nombre.isBlank() || correo.isBlank() || clave.isBlank()) {
            _mensaje.value = "Completa todos los campos"
            return
        }

        if (!correo.contains("@")) {
            _mensaje.value = "Ingresa un correo válido"
            return
        }

        if (clave.length < 8) {
            _mensaje.value = "La contraseña debe tener al menos 8 caracteres"
            return
        }

        viewModelScope.launch {
            _mensaje.value = ""

            try {
                val usuario = RegistroUsuario(
                    nombre = nombre.trim(),
                    correo = correo.trim(),
                    contrasena = clave
                )

                AdminRetrofitClient.adminApi.registrarUsuario(usuario)

                _mensaje.value = "Usuario registrado correctamente"

            } catch (e: HttpException) {
                _mensaje.value = when (e.code()) {
                    400 -> "Revisa los datos ingresados"
                    409 -> "El correo ya está registrado"
                    else -> "Error del servidor: ${e.code()}"
                }

            } catch (e: IOException) {
                _mensaje.value = "No se pudo conectar con el servidor"

            } catch (e: Exception) {
                _mensaje.value = "No se pudo registrar el usuario"
            }
        }
    }

    fun iniciarSesion(correo: String, clave: String) {
        if (correo.isBlank() || clave.isBlank()) {
            _mensaje.value = "Completa todos los campos"
            return
        }

        viewModelScope.launch {
            _mensaje.value = ""
            _sesionIniciada.value = false
            _usuarioActual.value = null
            _autorizacion.value = null

            val credenciales = "${correo.trim()}:$clave"
            val codigo = Base64.encodeToString(
                credenciales.toByteArray(Charsets.UTF_8),
                Base64.NO_WRAP
            )
            val encabezado = "Basic $codigo"

            try {
                val usuario = AdminRetrofitClient.adminApi.obtenerUsuario(encabezado)

                _usuarioActual.value = usuario
                _autorizacion.value = encabezado
                _sesionIniciada.value = true
                _mensaje.value = "Inicio de sesión correcto"

            } catch (e: HttpException) {
                _mensaje.value = when (e.code()) {
                    401 -> "Correo o contraseña incorrectos"
                    403 -> "No tienes permiso para ingresar"
                    else -> "Error del servidor: ${e.code()}"
                }

            } catch (e: IOException) {
                _mensaje.value = "No se pudo conectar con el servidor"

            } catch (e: Exception) {
                _mensaje.value = "No se pudo iniciar sesión"
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = ""
    }

    fun cerrarSesion() {
        _sesionIniciada.value = false
        _usuarioActual.value = null
        _autorizacion.value = null
        _mensaje.value = ""
    }
}
