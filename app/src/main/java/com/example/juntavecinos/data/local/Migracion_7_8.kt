package com.example.juntavecinos.data.local

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRACION_7_8 = object : Migration(7, 8) {

    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "ALTER TABLE reservas ADD COLUMN servidorId INTEGER"
        )
    }
}
