package com.example.juntavecinos.data.local

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRACION_5_6 = object : Migration(5, 6) {

    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "ALTER TABLE movimientos ADD COLUMN categoria TEXT NOT NULL DEFAULT 'Sin categoría'"
        )
    }
}


