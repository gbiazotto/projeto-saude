package com.fagundes.projetosaude.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fagundes.projetosaude.model.leituras.Pressao

@Dao
interface PressaoDAO {

    @Query("SELECT * FROM Pressao")
    fun all(): LiveData<List<Pressao>>

    @Insert
    fun salva(pressao: Pressao)

    @Query("SELECT * FROM Pressao ORDER BY data DESC LIMIT 1")
    fun ultimo(): LiveData<Pressao>

    @Query("SELECT * FROM Pressao WHERE enviado = 0")
    fun todosNaoEnciados(): List<Pressao>

}