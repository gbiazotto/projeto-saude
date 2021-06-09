package com.fagundes.projetosaude.ui.dashboard

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.fagundes.projetosaude.R
import kotlinx.android.synthetic.main.fragment_dashboard.view.*

class DashboardFragment : Fragment() {

    private lateinit var rootView: View

    private lateinit var dashboardViewModel: DashboardViewModel

    private var listaDevices = ArrayList<String>()

    private lateinit var handler: Handler

    private val listaDevicesView by lazy { rootView.listaDevices }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dashboardViewModel =
            ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false)

        handler = Handler()

        listaDevicesView.adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            listaDevices
        )



        return rootView
    }



}