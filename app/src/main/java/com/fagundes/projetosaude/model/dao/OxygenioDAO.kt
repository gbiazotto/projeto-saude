package com.fagundes.projetosaude.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fagundes.projetosaude.model.leituras.Oxygenio

@Dao
interface OxygenioDAO {


    @Query("SELECT * FROM Oxygenio")
    fun all(): LiveData<List<Oxygenio>>

    @Insert
    fun salva(oxygenio: Oxygenio)

    @Query("SELECT * FROM Oxygenio ORDER BY data DESC LIMIT 1")
    fun ultimo(): LiveData<Oxygenio>

    @Query("SELECT * FROM Oxygenio WHERE enviado = 0")
    fun todosNaoEnciados(): List<Oxygenio>


}