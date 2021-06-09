package com.fagundes.projetosaude.model.dtos

data class FrequenciaDTO(
    val id: Int,
    val data: String,
    val frequencia: Int,
    val localizacao: String,
    val longitude: Double,
    val latitude: Double,
    val idUsuario: Int
){
    override fun toString(): String {
        return "FrequenciaDTO(id=$id, data='$data', frequencia=$frequencia, localizacao='$localizacao', longitude=$longitude, latitude=$latitude, idUsuario=$idUsuario)"
    }
}