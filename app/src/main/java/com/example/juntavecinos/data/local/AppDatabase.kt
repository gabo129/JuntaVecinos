
package com.example.juntavecinos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.juntavecinos.data.model.MovimientoEntity
import com.example.juntavecinos.data.model.ReservaEntity
import com.example.juntavecinos.data.model.UsuarioEntity

@Database(
    entities = [
        ReservaEntity::class,
        MovimientoEntity::class,
        UsuarioEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reservaDao(): ReservaDao

    abstract fun movimientoDao(): MovimientoDao

    abstract fun usuarioDao(): UsuarioDao

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
                    .addMigrations(
                        MIGRACION_1_2,
                        MIGRACION_2_3,
                        MIGRACION_3_4,
                        MIGRACION_4_5,
                        MIGRACION_5_6
                    )
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
