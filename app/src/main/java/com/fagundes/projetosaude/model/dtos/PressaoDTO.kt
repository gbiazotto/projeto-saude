package com.fagundes.projetosaude.model.dtos

class PressaoDTO(
    val id: Int,
    val data: String,
    val alta: Int,
    val baixa: Int,
    val localizacao: String,
    val longitude: Double,
    val latitude: Double,
    val idUsuario: Int
){
    override fun toString(): String {
        return "PressaoDTO(id=$id, data='$data', alta=$alta, baixa=$baixa, localizacao='$localizacao', longitude=$longitude, latitude=$latitude, idUsuario=$idUsuario)"
    }
}