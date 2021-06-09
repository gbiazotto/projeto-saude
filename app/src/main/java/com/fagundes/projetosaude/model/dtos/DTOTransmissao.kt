package com.fagundes.projetosaude.model.dtos

data class DTOTransmissao (
    val token: String,
    val idUsuario: Int,
    val listaFrequencia: ArrayList<FrequenciaDTO>,
    val listaPressao: ArrayList<PressaoDTO>,
    val listaOxygenio: ArrayList<OxygenioDTO>
){
    override fun toString(): String {
        return "DTOTransmissao(token='$token', idUsuario=$idUsuario, listaFrequencia=$listaFrequencia, listaPressao=$listaPressao, listaOxygenio=$listaOxygenio)"
    }
}