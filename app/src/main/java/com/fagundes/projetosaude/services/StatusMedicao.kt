package com.fagundes.projetosaude.services

import androidx.lifecycle.MutableLiveData

class StatusMedicao {

    private constructor()

    val status = MutableLiveData<String>().apply {
                value =
                    NENHUM
    }

    val conexao = MutableLiveData<Boolean>().apply {
        value = false
    }

    var commandosBluetooth: CommandosBluetooth? = null

    companion object{

        const val FREQUENCIA = "Lendo Frequencia Cardiaca"
        const val PRESSAO = "Lendo Pressão Cardiaca"
        const val OXYGENIO = "Lendo o Oxigenio no sangue"
        const val NENHUM = " - "

        private lateinit var instance: StatusMedicao

        fun getStatusMedicao(): StatusMedicao {
            if(!Companion::instance.isInitialized){
                instance =
                    StatusMedicao()
            }
            return instance
        }
    }


}