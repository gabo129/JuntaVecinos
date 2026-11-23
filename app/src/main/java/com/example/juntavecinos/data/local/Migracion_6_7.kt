package com.example.juntavecinos.data.local

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRACION_6_7 = object : Migration(6, 7) {

    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "ALTER TABLE movimientos ADD COLUMN servidorId INTEGER"
        )
    }
}


