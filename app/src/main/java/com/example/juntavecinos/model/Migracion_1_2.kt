package com.example.juntavecinos.model

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRACION_1_2 = object : Migration(1, 2) {

    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS movimientos (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                descripcion TEXT NOT NULL,
                monto INTEGER NOT NULL,
                fecha INTEGER NOT NULL,
                esIngreso INTEGER NOT NULL,
                confirmado INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}