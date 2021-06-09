package com.fagundes.projetosaude.utils

import java.util.*

class Data {

    companion object{
        fun getHora(time: Long): String {
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

        fun getHoraFirebase(time: Long): String {
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
                "${c[Calendar.DAY_OF_MONTH]}/${c[Calendar.MONTH] + 1}/${c[Calendar.YEAR]}T${hora}:${min}:${sec}"
            return data
        }

    }

}