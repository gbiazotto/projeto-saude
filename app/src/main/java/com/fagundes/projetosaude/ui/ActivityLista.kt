package com.fagundes.projetosaude.ui

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.fagundes.projetosaude.R
import com.fagundes.projetosaude.model.banco.Banco
import com.fagundes.projetosaude.model.leituras.Frequencia
import com.fagundes.projetosaude.model.leituras.Oxygenio
import com.fagundes.projetosaude.model.leituras.Pressao
import kotlinx.android.synthetic.main.activity_lista.*
import kotlinx.android.synthetic.main.item_lista_medida.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ActivityLista : AppCompatActivity() {

    var listaPressao = ArrayList<Pressao>()

    var listaFrequencia = ArrayList<Frequencia>()

    var listaOxygenio = ArrayList<Oxygenio>()

    var listaAUsar = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista)


        val banco = Banco.getDataBase(this)

        listaAUsar = intent.extras?.get("medida") as String

        when (listaAUsar) {
            "pressao" -> {

                banco.pressaoDAO().all().observe(this, androidx.lifecycle.Observer {
                    if (it != null) {
                        listaPressao = it as ArrayList<Pressao>
                        listaMedidas.adapter = Adapter()
                        listaMedidas.layoutManager = LinearLayoutManager(this@ActivityLista)
                    }
                })

            }
            "frequencia" -> {

                banco.frequenciaDAO().all().observe(this, androidx.lifecycle.Observer {
                    if (it != null) {
                        listaFrequencia = it as ArrayList<Frequencia>
                        listaMedidas.adapter = Adapter()
                        listaMedidas.layoutManager = LinearLayoutManager(this@ActivityLista)
                    }
                })
            }
            "oxygenio" -> {

                banco.oxygenioDAO().all().observe(this, androidx.lifecycle.Observer {
                    if (it != null) {
                        listaOxygenio = it as ArrayList<Oxygenio>
                        listaMedidas.adapter = Adapter()
                        listaMedidas.layoutManager = LinearLayoutManager(this@ActivityLista)
                    }
                })

            }
        }


    }


    inner class Adapter() : RecyclerView.Adapter<Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(
                LayoutInflater.from(this@ActivityLista).inflate(
                    R.layout.item_lista_medida,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return when (listaAUsar) {
                "pressao" -> {
                    listaPressao.count()
                }
                "frequencia" -> {
                    listaFrequencia.count()
                }
                "oxygenio" -> {
                    listaOxygenio.count()
                }
                else -> {
                    0
                }
            }
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            when (listaAUsar) {
                "pressao" -> {

                    val u = listaPressao[position]
                    holder.valor.text = "Pressao = ${u.alta}/${u.baixa}"
                    holder.data.text = getHora(u.data)
                    holder.localizacao.text =
                        getEndereco(u.latitude, u.longitude)
                }
                "frequencia" -> {
                    val u = listaFrequencia[position]
                    holder.valor.text = "Frequencia = ${u.frequencia}"
                    holder.data.text = getHora(u.data)
                    holder.localizacao.text =
                        getEndereco(u.latitude, u.longitude)
                }
                "oxygenio" -> {
                    val u = listaOxygenio[position]
                    holder.valor.text = "Oxygenio = ${u.valor}%"
                    holder.data.text = getHora(u.data)
                    holder.localizacao.text =
                        getEndereco(u.latitude, u.longitude)
                }
            }
        }

    }

    private fun getEndereco(lat: Double, long: Double): String {

        if (lat == 0.0 || long == 0.0) {
            return " - "
        }


        val geocoder = Geocoder(this, Locale.getDefault())
        val endereco = geocoder.getFromLocation(lat, long, 1)

        var retorno = ""

        if (endereco.isNotEmpty()) {
            retorno += endereco[0].getAddressLine(0)
//            retorno += endereco[0].getAddressLine(1) + "  --  "
//            retorno += endereco[0].getAddressLine(2)
        }

        return retorno
    }

    private fun getHora(time: Long): String {
        val c = Calendar.getInstance()
        c.timeInMillis = time

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

        val data =
            "data: ${c[Calendar.DAY_OF_MONTH]}/${c[Calendar.MONTH] + 1}/${c[Calendar.YEAR]} as ${hora}:${min}:${sec}"
        return data
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {

        val valor = view.valor
        val data = view.data
        val localizacao = view.localizacao

    }

}
