package com.fagundes.projetosaude.model

import android.content.Context

class Preferencias {

 val PREF = "PORJETO_SAUDE"
 val USUARIO = "USUARIO"

    fun salvaUsuario(context: Context, nome: String){
        val sharedPref = context.getSharedPreferences(
            PREF, Context.MODE_PRIVATE)

        sharedPref.edit().putString(USUARIO, nome).apply()

    }

    fun getUsuario(context: Context): String{
        val sharedPref = context.getSharedPreferences(
            PREF, Context.MODE_PRIVATE)

        return sharedPref.getString(USUARIO,"usuario") ?: "usuario"

    }


}