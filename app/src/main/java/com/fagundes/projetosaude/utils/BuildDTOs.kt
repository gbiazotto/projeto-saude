package com.fagundes.projetosaude.utils

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fagundes.projetosaude.model.Leituras
import com.fagundes.projetosaude.model.Preferencias
import com.fagundes.projetosaude.model.banco.Banco
import com.fagundes.projetosaude.model.dtos.DTOTransmissao
import com.fagundes.projetosaude.model.dtos.FrequenciaDTO
import com.fagundes.projetosaude.model.dtos.OxygenioDTO
import com.fagundes.projetosaude.model.dtos.PressaoDTO
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class BuildDTOs(
    val context: Context,
    val banco: Banco
) {

    private val idUsuario = 0
    private val token = "asdfasdfasdfasdfadsfasdfasdf"

    init {
        //TODO recuperar idUsuario e token
    }

    fun getDTOTransmissao(): MutableLiveData<DTOTransmissao> {
        val retorno = MutableLiveData<DTOTransmissao>()

        Log.i("dto", "getDtoTransmissao")


        GlobalScope.launch(context = Dispatchers.IO) {

            val listaPressao = montaListaPressaoDTO().await()

            val listaOxygenio = montaListaOxygenioDTO().await()

            val listaFrequencia = montaListaFrequenciaDTO().await()

            val dto = DTOTransmissao(
                token,
                idUsuario,
                listaFrequencia,
                listaPressao,
                listaOxygenio
            )

            Log.i("dto", "retornando $dto")

            val usuario = Preferencias().getUsuario(context)

            val hora = getDataHoraFormatada(System.currentTimeMillis()).replace(" ", "_")

            val database = FirebaseDatabase.getInstance()

            database.getReferenceFromUrl("https://biosat-teste.firebaseio.com/app/$usuario/")
                .setValue(dto)

            database.getReferenceFromUrl("https://biosat-teste.firebaseio.com/leituras/$usuario/")
                .setValue(
                    getLeituras(dto)
                ) { error, _ ->
                    run {
                        if (error == null) {

                            GlobalScope.async {
                                val daoFrequencia = banco.frequenciaDAO()

                                var listaFrequencia = daoFrequencia.todosNaoEnciados()
                                for (f in listaFrequencia) {
                                    f.enviado = 1
                                    daoFrequencia.salva(f)
                                }


                                val daoPressao = banco.pressaoDAO()

                                var listaPressao = daoPressao.todosNaoEnciados()
                                for (f in listaPressao) {
                                    f.enviado = 1
                                    daoPressao.salva(f)
                                }

                                val daoOxygenio = banco.oxygenioDAO()

                                var listaOxygenio = daoOxygenio.todosNaoEnciados()
                                for (f in listaOxygenio) {
                                    f.enviado = 1
                                    daoOxygenio.salva(f)
                                }


                            }
                        }

                    }
                }
        }

        return retorno
    }

    private fun getLeituras(dto: DTOTransmissao): ArrayList<Leituras> {

        val lista = ArrayList<Leituras>()

        for (f in dto.listaFrequencia) {
            lista.add(
                Leituras(
                    f.frequencia,
                    f.data.substring(11, 16),
                    2,
                    11,
                    9,
                    f.latitude,
                    f.longitude,
                    0,
                    0,
                    0,
                    1,
                    3
                )
            )
        }


        for (i in 0 until dto.listaPressao.size) {

            if (i >= lista.size) {
                break
            }

            lista[i].pressaoa = dto.listaPressao[i].alta
            lista[i].pressaob = dto.listaPressao[i].baixa

        }

        for (i in 0 until dto.listaOxygenio.size) {

            if (i >= lista.size) {
                break
            }

            lista[i].oximetria = dto.listaOxygenio[i].saturacao

        }


        return lista
    }


    private fun montaListaFrequenciaDTO(): Deferred<ArrayList<FrequenciaDTO>> =
        GlobalScope.async {

            val lista = ArrayList<FrequenciaDTO>()

            var listaFrequencia = banco.frequenciaDAO().todosNaoEnciados()

            for (f in listaFrequencia) {

                lista.add(
                    FrequenciaDTO(
                        f.data.toInt(),
                        getDataHoraFormatada(f.data),
                        f.frequencia,
                        getEndereco(f.latitude, f.longitude),
                        f.longitude,
                        f.latitude,
                        idUsuario

                    )
                )
            }

            return@async lista
        }

    private fun montaListaPressaoDTO(): Deferred<ArrayList<PressaoDTO>> = GlobalScope.async {

        val lista = ArrayList<PressaoDTO>()

        var listaPressao = banco.pressaoDAO().todosNaoEnciados()

        for (f in listaPressao) {

            lista.add(
                PressaoDTO(
                    f.data.toInt(),
                    getDataHoraFormatada(f.data),
                    f.alta,
                    f.baixa,
                    getEndereco(f.latitude, f.longitude),
                    f.longitude,
                    f.latitude,
                    idUsuario
                )
            )
        }


        return@async lista
    }

    private fun montaListaOxygenioDTO(): Deferred<ArrayList<OxygenioDTO>> = GlobalScope.async {

        val lista = ArrayList<OxygenioDTO>()

        var listaOxygenio = banco.oxygenioDAO().todosNaoEnciados()

        for (f in listaOxygenio) {

            lista.add(
                OxygenioDTO(
                    f.data.toInt(),
                    getDataHoraFormatada(f.data),
                    f.valor,
                    getEndereco(f.latitude, f.longitude),
                    f.longitude,
                    f.latitude,
                    idUsuario
                )
            )
        }

        return@async lista
    }

    private fun getEndereco(lat: Double, long: Double): String {

        if (lat == 0.0 || long == 0.0) {
            return " - "
        }

        val geocoder = Geocoder(context, Locale.getDefault())
        val endereco = geocoder.getFromLocation(lat, long, 1)

        var retorno = ""

        if (endereco.isNotEmpty()) {
            retorno += endereco[0].getAddressLine(0)
        }

        if (retorno.length >= 500) {
            return retorno.substring(0, 500)
        }

        return retorno

    }

    private fun getDataHoraFormatada(t: Long): String {
        val c = Calendar.getInstance()
        c.timeInMillis = t

        var hora = if (c[Calendar.HOUR_OF_DAY] < 10) {
            "0" + c[Calendar.HOUR_OF_DAY]
        } else {
            c[Calendar.HOUR_OF_DAY]
        }

        var min = if (c[Calendar.MINUTE] < 10) {
            "0" + c[Calendar.MINUTE]
        } else {
            c[Calendar.MINUTE]
        }

        var sec = if (c[Calendar.SECOND] < 10) {
            "0" + c[Calendar.SECOND]
        } else {
            c[Calendar.SECOND]
        }

        val dia = if (c[Calendar.DAY_OF_MONTH] < 10) {
            "0" + c[Calendar.DAY_OF_MONTH]
        } else {
            c[Calendar.DAY_OF_MONTH]
        }

        val mes = if ((c[Calendar.MONTH] + 1) < 10) {
            "0" + (c[Calendar.MONTH] + 1)
        } else {
            (c[Calendar.MONTH] + 1)
        }

        val data =
            "${c[Calendar.YEAR]}-$mes-${dia}T${hora}:${min}:${sec}:${c[Calendar.MILLISECOND]}"
        return data
    }

}