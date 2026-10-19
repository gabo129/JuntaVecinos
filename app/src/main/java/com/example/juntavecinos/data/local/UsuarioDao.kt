package com.example.juntavecinos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.juntavecinos.data.model.UsuarioEntity

@Dao
interface UsuarioDao {

    @Insert
    suspend fun guardarUsuario(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios WHERE nombreUsuario = :nombreUsuario LIMIT 1")
    suspend fun buscarUsuario(nombreUsuario: String): UsuarioEntity?

}