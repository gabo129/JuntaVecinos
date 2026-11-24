package com.example.juntavecinos.data.remote

import com.example.juntavecinos.data.model.RegistroUsuario
import com.example.juntavecinos.data.model.UsuarioServidor
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AdminApi {

    @POST("api/usuarios")
    suspend fun registrarUsuario(
        @Body usuario: RegistroUsuario
    ): UsuarioServidor

    @GET("api/auth/me")
    suspend fun obtenerUsuario(
        @Header("Authorization") credenciales: String
    ): UsuarioServidor
}
