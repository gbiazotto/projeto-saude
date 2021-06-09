package com.fagundes.projetosaude.model.leituras

import androidx.room.Entity

@Entity
class Pressao(

    @androidx.room.PrimaryKey(autoGenerate = false)
    var data: Long = 0,
    var alta: Int = 0,
    var baixa: Int = 0,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var enviado: Int = 0){
    override fun toString(): String {
        return "Pressao(data=$data, alta=$alta, baixa=$baixa, latitude=$latitude, longitude=$longitude)"
    }
}
