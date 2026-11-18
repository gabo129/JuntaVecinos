package com.example.juntavecinos.data.local

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRACION_4_5 = object : Migration(4, 5) {

    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "ALTER TABLE reservas ADD COLUMN estado TEXT NOT NULL DEFAULT 'Pendiente'"
        )
    }
}
