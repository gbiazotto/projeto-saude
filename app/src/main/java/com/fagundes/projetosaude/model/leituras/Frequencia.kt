package com.fagundes.projetosaude.model.leituras

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class Frequencia(
    @PrimaryKey(autoGenerate = false)
    var data: Long = 0,
    var frequencia: Int = 0,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var enviado: Int = 0){

    override fun toString(): String {
        return "Frequencia(data=$data, frequencia=$frequencia, latitude=$latitude, longitude=$longitude)"
    }
}

