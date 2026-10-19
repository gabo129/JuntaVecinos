package com.example.juntavecinos.data.repository

import com.example.juntavecinos.data.local.UsuarioDao
import com.example.juntavecinos.data.model.UsuarioEntity
import com.example.juntavecinos.data.SeguridadClave

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    suspend fun registrarUsuario(nombre: String, clave: String): Boolean {
        if (usuarioDao.buscarUsuario(nombre) != null) {
            return false
        }

        val salt = SeguridadClave.generarSalt()
        val hash = SeguridadClave.generarHash(clave, salt)

        usuarioDao.guardarUsuario(
            UsuarioEntity(
                nombreUsuario = nombre,
                claveHash = hash,
                salt = salt
            )
        )

        return true
    }

    suspend fun iniciarSesion(nombre: String, clave: String): Boolean {
        val usuario = usuarioDao.buscarUsuario(nombre) ?: return false

        val hash = SeguridadClave.generarHash(clave, usuario.salt)

        return hash == usuario.claveHash
    }
}