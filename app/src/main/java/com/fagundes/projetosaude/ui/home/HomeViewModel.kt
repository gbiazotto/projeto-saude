package com.fagundes.projetosaude.ui.home

import androidx.lifecycle.ViewModel
import com.fagundes.projetosaude.model.Repositorio

class HomeViewModel constructor(
    repositorio: Repositorio
) : ViewModel() {

    val frequencia = repositorio.frequencia

    val pressao =  repositorio.pressao

    val oxygenio =  repositorio.oxygenio
}
