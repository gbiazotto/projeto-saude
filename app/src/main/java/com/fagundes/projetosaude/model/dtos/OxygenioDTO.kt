package com.fagundes.projetosaude.model.dtos

class OxygenioDTO(
    val id: Int,
    val data: String,
    val saturacao: Int,
    val localizacao: String,
    val longitude: Double,
    val latitude: Double,
    val idUsuario: Int
){
    override fun toString(): String {
        return "OxygenioDTO(id=$id, data='$data', saturacao=$saturacao, localizacao='$localizacao', longitude=$longitude, latitude=$latitude, idUsuario=$idUsuario)"
    }
}