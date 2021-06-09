package com.fagundes.projetosaude.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fagundes.projetosaude.MainActivity
import com.fagundes.projetosaude.R
import com.fagundes.projetosaude.model.Repositorio
import com.fagundes.projetosaude.services.StatusMedicao
import com.fagundes.projetosaude.ui.ActivityBusca
import com.fagundes.projetosaude.ui.ActivityLista
import com.fagundes.projetosaude.utils.Data
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this,
                MyViewModelFactory(
                    activity!!
                )
            ).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val activity: MainActivity? = (activity as MainActivity)

        StatusMedicao.getStatusMedicao().status.observe(this, Observer {
            statusMedicao.text = it
        })

//        if (activity != null) {
//            homeViewModel.repositorio = Repositorio.getRepositorio(activity)
//        }

        homeViewModel.frequencia.observe(this, Observer {
            if (it != null) {
                valorFrequencia.text = "${it.frequencia} BPM"
                ulitmaMedicao.text = "Ultima medição:\n" + Data.getHora(it.data)
            }
        })

        homeViewModel.pressao.observe(this, Observer {
            if (it != null) {
                valorPressão.text = "${it.alta}/${it.baixa}"
                ulitmaMedicao.text = "Ultima medição:\n" + Data.getHora(it.data)
            }
        })

        homeViewModel.oxygenio.observe(this, Observer {
            if (it != null) {
                valorOxygenio.text = "${it.valor}%"
                ulitmaMedicao.text = "Ultima medição:\n" + Data.getHora(it.data)
            }
        })

        StatusMedicao.getStatusMedicao().conexao.observe(this, Observer {
            if(it){
                buttonBuscar.visibility = View.GONE
                root.buttonLerDados.visibility = View.VISIBLE
            }else{
                buttonBuscar.visibility = View.VISIBLE
                root.buttonLerDados.visibility = View.GONE
            }
        })

        root.buttonBuscar.setOnClickListener {
            activity?.startActivity(Intent(activity,ActivityBusca::class.java))
        }

        root.dadosFrequencia.setOnClickListener {
            val intent = Intent(activity, ActivityLista::class.java)
            intent.putExtra("medida","frequencia")
            activity?.startActivity(intent)
        }

        root.dadosPressao.setOnClickListener {
            val intent = Intent(activity, ActivityLista::class.java)
            intent.putExtra("medida","pressao")
            activity?.startActivity(intent)
        }

        root.dadosOxygenio.setOnClickListener {
            val intent = Intent(activity, ActivityLista::class.java)
            intent.putExtra("medida","oxygenio")
            activity?.startActivity(intent)
        }

        root.buttonLerDados.setOnClickListener {
            StatusMedicao.getStatusMedicao().commandosBluetooth?.connectou(true)
        }

        return root
    }

    class MyViewModelFactory(private val context: Context) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return HomeViewModel(
                Repositorio.getRepositorio(
                    context
                )
            ) as T
        }

    }


}