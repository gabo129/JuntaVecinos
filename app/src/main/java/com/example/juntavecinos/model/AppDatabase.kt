package com.example.juntavecinos.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ReservaEntity::class,
        MovimientoEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reservaDao(): ReservaDao

    abstract fun movimientoDao(): MovimientoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "juntavecinos_database"
                )
                    .addMigrations(MIGRACION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}