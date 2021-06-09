package com.fagundes.projetosaude.model.leituras

import androidx.room.Entity

@Entity
class Oxygenio(
    @androidx.room.PrimaryKey(autoGenerate = false)
    var data: Long = 0,
    var valor: Int = 0,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var enviado: Int = 0
) {

    override fun toString(): String {
        return "Oxygenio(data=$data, valor=$valor, latitude=$latitude, longitude=$longitude)"
    }
}
