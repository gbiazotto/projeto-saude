package com.fagundes.projetosaude.services

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.fagundes.projetosaude.model.Repositorio
import com.fagundes.projetosaude.model.leituras.Frequencia
import com.fagundes.projetosaude.model.leituras.Oxygenio
import com.fagundes.projetosaude.model.leituras.Pressao
import com.google.android.gms.location.FusedLocationProviderClient

class CommandosBluetooth(
    private val service: ServiceBluetoothLE,
    location: FusedLocationProviderClient?,
    private val repositorio: Repositorio
) {

    var latitude: Double = 0.0
    var longitude: Double = 0.0
    private val handler = Handler(Looper.getMainLooper())
    private val statusMedicao = StatusMedicao.getStatusMedicao().status

    init {
        location?.lastLocation?.addOnSuccessListener {
            latitude = it?.latitude ?: 0.0
            longitude = it?.longitude ?: 0.0
        }
    }

    private fun leituraFrequencia(inicia: Boolean) {
        val bytes = ByteArray(7)
        bytes[0] = 0xAB.toByte()
        bytes[1] = 0.toByte()
        bytes[2] = 4.toByte()
        bytes[3] = 0xFF.toByte()
        bytes[4] = 0x31.toByte()
        bytes[5] = TIPO_FREQUENCIA

        if (inicia) {
            bytes[6] = 1
            statusMedicao.postValue(StatusMedicao.FREQUENCIA)
        } else {
            bytes[6] = 0
            statusMedicao.postValue(StatusMedicao.NENHUM)
        }

        service.writeRXCharacteristic(bytes)
    }

    private fun leituraPressao(inicia: Boolean) {
        Log.i("leitura", "Leitura Frequencia iniciada")

        val bytes = ByteArray(7)
        bytes[0] = 0xAB.toByte()
        bytes[1] = 0.toByte()
        bytes[2] = 4.toByte()
        bytes[3] = 0xFF.toByte()
        bytes[4] = 0x31.toByte()
        bytes[5] = TIPO_PRESSAO

        if (inicia) {
            bytes[6] = 1
            statusMedicao.postValue(StatusMedicao.PRESSAO)
        } else {
            bytes[6] = 0
            statusMedicao.postValue(StatusMedicao.NENHUM)
        }

        Log.i("leitura", "Leitura Frequencia iniciada -- $bytes")

        service.writeRXCharacteristic(bytes)
    }

    private fun leituraOxygenio(inicia: Boolean) {
        val bytes = ByteArray(7)
        bytes[0] = 0xAB.toByte()
        bytes[1] = 0.toByte()
        bytes[2] = 4.toByte()
        bytes[3] = 0xFF.toByte()
        bytes[4] = 0x31.toByte()
        bytes[5] = TIPO_OXYGENIO

        if (inicia) {
            bytes[6] = 1
            statusMedicao.postValue(StatusMedicao.OXYGENIO)
        } else {
            bytes[6] = 0
            statusMedicao.postValue(StatusMedicao.NENHUM)
        }

        service.writeRXCharacteristic(bytes)
    }

    fun novosDados(datas: MutableList<Int>) {
        Log.i("Leitura", "novosDados")

        if (datas[0] != 0xAB && datas[4] != 0x31) {
            //não eh nenhum dado importante atualmente
            return
        } else if (datas[6] > 0) {
            Log.i("Leitura", "Passou no if")

            if (datas[5] == TIPO_FREQUENCIA.toInt()) {
                val frequencia = Frequencia(
                    getTime(),
                    datas[6],
                    latitude,
                    longitude
                )

                Log.i("Leitura", "valor salvo: $frequencia")

                repositorio.setFrequencia(frequencia)

                leituraFrequencia(false)

                handler.postDelayed({
                    //Liga pressao
                    leituraPressao(true)
                }, 1000)
                return
            }

            if (datas[5] == TIPO_PRESSAO.toInt()) {
                val pressao = Pressao(
                    getTime(),
                    datas[6],
                    datas[7],
                    latitude,
                    longitude
                )

                repositorio.setPressao(pressao)

                Log.i("Leitura", "valor salvo: $pressao")

                leituraPressao(false)

                handler.postDelayed({
                    //Liga oxygenio
                    leituraOxygenio(true)
                }, 1000)

                return
            }

            if (datas[5] == TIPO_OXYGENIO.toInt()) {
                val oxygenio = Oxygenio(
                    getTime(),
                    datas[6],
                    latitude,
                    longitude
                )

                repositorio.setOxygenio(oxygenio)

                Log.i("Leitura", "valor salvo: $oxygenio")

                leituraOxygenio(false)

                service.iniciaEnvioDeDados()

                return
            }
        }
    }

    fun connectou(b: Boolean) {
        if (statusMedicao.value != null && statusMedicao.value == StatusMedicao.NENHUM) {
            StatusMedicao.getStatusMedicao().commandosBluetooth = this
            leituraFrequencia(b)
        }
    }

    private fun getTime() = System.currentTimeMillis()

    companion object {

        const val TIPO_FREQUENCIA: Byte = 0x0A
        const val TIPO_PRESSAO: Byte = 0x22
        const val TIPO_OXYGENIO: Byte = 0x12
    }
}
