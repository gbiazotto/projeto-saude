package com.fagundes.projetosaude.ui

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fagundes.projetosaude.R
import com.fagundes.projetosaude.services.ServiceBluetoothLE
import com.fagundes.projetosaude.services.StatusMedicao
import kotlinx.android.synthetic.main.activity_busca.*
import kotlinx.android.synthetic.main.item_lista_device.view.*
import java.util.*

class ActivityBusca : AppCompatActivity() {

    lateinit var handler: Handler
    private var mScanning: Boolean = false
    private var listaDevices = ArrayList<BluetoothDevice>()
    val bluetoothAdapter by lazy { bluetoothManager.adapter }
    val bluetoothManager by lazy { getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager }
    val bluetoothLeScanner by lazy { bluetoothAdapter.bluetoothLeScanner }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_busca)

        handler = Handler()
        bluetoothAdapter.enable()

        recyclerView.adapter = Adapter()
        recyclerView.layoutManager = LinearLayoutManager(this)

        verificaPermissoes()

        StatusMedicao.getStatusMedicao().conexao.observe(this, androidx.lifecycle.Observer {
            if (it) {
                finish()
            }
        })
    }

    private fun verificaPermissoes() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 321
                )
            }
        } else {
            verificaPermissoesBluetooth()
        }
    }

    private fun verificaPermissoesBluetooth() {
        bluetoothAdapter.enable()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADMIN
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.BLUETOOTH_ADMIN
                )
            ) {

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_ADMIN), 321
                )
            }
        } else {
            iniciaBusca(true)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        verificaPermissoes()
    }

    override fun onResume() {
        super.onResume()
        iniciaBusca(true)
    }

    override fun onStop() {
        super.onStop()
        iniciaBusca(false)
    }

    private val mLeScanCallback by lazy {
        object : ScanCallback() {
            override fun onScanFailed(errorCode: Int) {
                Log.i("scanner", "onScanFailed $errorCode")
                super.onScanFailed(errorCode)
            }

            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                Log.i("scanner", "onScanResult $result")
                super.onScanResult(callbackType, result)
                if (result?.device?.name == "C6T-115F" &&
                    !listaDevices.contains(result.device)
                ) {
                    handler.post {
                        listaDevices.add(result.device)
                        Toast.makeText(
                            this@ActivityBusca,
                            "Dispositivo Encontrado",
                            Toast.LENGTH_LONG
                        ).show()
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                Log.i("scanner", "onBatchScanResults $results")
                super.onBatchScanResults(results)
            }
        }
    }

    private fun iniciaBusca(enable: Boolean) {
        when (enable) {
            true -> {
                bluetoothLeScanner?.startScan(
                    mLeScanCallback
                )
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this@ActivityBusca, "Iniciando Buscas", Toast.LENGTH_LONG)
                        .show()
                }
                handler.postDelayed({
                    mScanning = false
                    bluetoothLeScanner?.stopScan(mLeScanCallback)
                    listaDevices.clear()
                }, 40000)
                mScanning = true
            }
            else -> {
                mScanning = false
                bluetoothLeScanner?.stopScan(mLeScanCallback)
                handler.removeCallbacksAndMessages(null)
                listaDevices.clear()
            }
        }
    }

    inner class Adapter : RecyclerView.Adapter<Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(
                LayoutInflater.from(this@ActivityBusca).inflate(
                    R.layout.device_item_list,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount() = listaDevices.count()

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val d = listaDevices[position]
            holder.nome.text = d.name
            holder.mac.text = d.address
            holder.device = d
        }
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val nome = view.tv_device_name
        val mac = view.tv_device_mac_address
        lateinit var device: BluetoothDevice

        init {
            view.setOnClickListener {
                conectar(device)
            }
        }
    }

    fun conectar(device: BluetoothDevice) {
        val intent = Intent(this, ServiceBluetoothLE::class.java)
        intent.putExtra("device", device.address)
        startService(intent)
    }
}
