package com.example.juntavecinos.data.local

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRACION_3_4 = object : Migration(3, 4) {

    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "ALTER TABLE reservas ADD COLUMN dependencia TEXT NOT NULL DEFAULT ''"
        )
    }
}
