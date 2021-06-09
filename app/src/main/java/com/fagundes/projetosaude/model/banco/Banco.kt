package com.fagundes.projetosaude.model.banco

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fagundes.projetosaude.model.dao.FrequenciaDAO
import com.fagundes.projetosaude.model.dao.OxygenioDAO
import com.fagundes.projetosaude.model.dao.PressaoDAO
import com.fagundes.projetosaude.model.leituras.Frequencia
import com.fagundes.projetosaude.model.leituras.Oxygenio
import com.fagundes.projetosaude.model.leituras.Pressao

@Database(
    entities = [Pressao::class, Oxygenio::class, Frequencia::class],
    version = 1,
    exportSchema = false
)
abstract class Banco : RoomDatabase() {

    abstract fun frequenciaDAO(): FrequenciaDAO
    abstract fun pressaoDAO(): PressaoDAO
    abstract fun oxygenioDAO(): OxygenioDAO

    companion object {

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE IF EXISTS Frequencia;")
                database.execSQL("DROP TABLE IF EXISTS Oxygenio;")
                database.execSQL("DROP TABLE IF EXISTS Pressao;")
            }
        }

        fun getDataBase(context: Context): Banco {
            return Room.databaseBuilder(context, Banco::class.java, "projeto-saude")
//                .addMigrations(MIGRATION_1_2)
                .build()
        }

    }

}