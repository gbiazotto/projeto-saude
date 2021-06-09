package com.fagundes.projetosaude

import android.util.Log
import java.util.*
import kotlin.experimental.and

class NewDataHandlerUtil {

    fun addBytes(data1: ByteArray, data2: ByteArray?): ByteArray? {
        var data3 = data1
        if (data2 != null && data2.isNotEmpty()) {
            data3 = ByteArray(data1.size + data2.size)
            System.arraycopy(data1, 0, data3, 0, data1.size)
            System.arraycopy(data2, 0, data3, data1.size, data2.size)
        }
        return data3
    }

    fun bytesToHexStr(bytes: ByteArray?): String? {
        val stringBuilder = StringBuilder("")
        if (bytes == null || bytes.isEmpty()) {
            return null
        }
        for (i in bytes.indices) {
            val v: Byte = bytes[i] and 0xFF.toByte()
            val hv = Integer.toHexString(v.toInt())
            if (hv.length < 2) {
                stringBuilder.append(" 0$hv")
            } else {
                stringBuilder.append(" $hv")
            }
        }
        return stringBuilder.toString()
    }

    fun bytesToArrayList(bytes: ByteArray): List<Int>? {
        val datas: MutableList<Int> = ArrayList()
        for (i in bytes.indices) {
            datas.add((bytes[i] and 0xff.toByte()).toInt())
        }
        return datas
    }

    fun bytesToArrayListForEcg(bytes: ByteArray): List<Int>? {
        val datas: MutableList<Int> = ArrayList()
        for (aByte in bytes) {
            Log.i("zgy", (aByte and 0xff.toByte()).toString())
        }
        for (i in bytes.indices) {
            if (i % 2 == 0 && bytes.size % 2 == 0) {
                datas.add((bytes[i] and 0xff.toByte()) + (bytes[i + 1] and 0xff.toByte()) * 256)
            }
        }
        return datas
    }
}