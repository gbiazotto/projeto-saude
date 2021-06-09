package com.fagundes.projetosaude.model

class Leituras(
    var bpm: Int,
    var hora_processo: String,
    var id_cliente: Int = 2,
    var id_processo: Int = 11,
    var id_pulseira: Int = 9,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var oximetria: Int,
    var pressaoa: Int,
    var pressaob: Int,
    var status: Int = 1,
    var usuario_processo: Int = 3
) 