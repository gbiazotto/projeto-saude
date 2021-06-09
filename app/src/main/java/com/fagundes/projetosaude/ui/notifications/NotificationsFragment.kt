package com.fagundes.projetosaude.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.fagundes.projetosaude.R
import com.fagundes.projetosaude.model.Preferencias
import kotlinx.android.synthetic.main.fragment_notifications.view.*

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)



        root.botaoSalvar.setOnClickListener {
            if (root.editText.text.isNotEmpty())
            Preferencias().salvaUsuario(requireContext(),root.editText.text.toString())
            Toast.makeText(requireContext(),"Usuario Salvo!",Toast.LENGTH_SHORT).show()
        }

        return root
    }
}