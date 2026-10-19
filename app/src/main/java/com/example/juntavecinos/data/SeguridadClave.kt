package com.example.juntavecinos.data

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

object SeguridadClave {

    fun generarSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)

        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    fun generarHash(clave: String, salt: String): String {
        val texto = salt + clave

        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(texto.toByteArray(Charsets.UTF_8))

        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }
}