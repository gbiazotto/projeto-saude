package com.fagundes.projetosaude.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.fagundes.projetosaude.model.banco.Banco
import com.fagundes.projetosaude.model.leituras.Frequencia
import com.fagundes.projetosaude.model.leituras.Oxygenio
import com.fagundes.projetosaude.model.leituras.Pressao

class Repositorio private constructor(context: Context) {

    private val banco = Banco.getDataBase(context)

    val pressao = MutableLiveData<Pressao>().apply {
        postValue(banco.pressaoDAO().ultimo().value)
    }

    val frequencia = MutableLiveData<Frequencia>().apply {
        postValue(banco.frequenciaDAO().ultimo().value)
    }

    val oxygenio = MutableLiveData<Oxygenio>().apply {
        postValue(banco.oxygenioDAO().ultimo().value)
    }

    fun setPressao(p: Pressao) {
        banco.pressaoDAO().salva(p)
        pressao.postValue(p)
    }

    fun setFrequencia(f: Frequencia) {
        banco.frequenciaDAO().salva(f)
        frequencia.postValue(f)
    }

    fun setOxygenio(o: Oxygenio) {
        banco.oxygenioDAO().salva(o)
        oxygenio.postValue(o)
    }

    companion object {

        private lateinit var instance: Repositorio

        fun getRepositorio(context: Context): Repositorio {
            if (!Companion::instance.isInitialized) {
                instance =
                    Repositorio(
                        context
                    )
            }
            return instance
        }
    }
}
