package com.fagundes.projetosaude.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fagundes.projetosaude.model.leituras.Frequencia

@Dao
interface FrequenciaDAO {

    @Query("SELECT * FROM Frequencia")
    fun all(): LiveData<List<Frequencia>>

    @Insert
    fun salva(frequencia: Frequencia)

    @Query("SELECT * FROM Frequencia ORDER BY data DESC LIMIT 1")
    fun ultimo(): LiveData<Frequencia>

    @Query("SELECT * FROM Frequencia WHERE enviado = 0")
    fun todosNaoEnciados(): List<Frequencia>


}