package com.fagundes.projetosaude.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fagundes.projetosaude.R
import com.fagundes.projetosaude.services.ServiceBluetoothLE
import com.fagundes.projetosaude.services.StatusMedicao
import kotlinx.android.synthetic.main.activity_new_search_bracelet.*
import kotlinx.android.synthetic.main.item_lista_device.view.*
import java.util.*

class NewSearchBraceletActivity : AppCompatActivity() {

    lateinit var handler: Handler
    private var mScanning: Boolean = false
    private var deviceList = ArrayList<BluetoothDevice>()
    private val bluetoothAdapter: BluetoothAdapter by lazy { bluetoothManager.adapter }
    private val bluetoothManager: BluetoothManager by lazy { getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager }
    private val bluetoothLeScanner: BluetoothLeScanner by lazy { bluetoothAdapter.bluetoothLeScanner }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_search_bracelet)

        handler = Handler()
        bluetoothAdapter.enable()

        rv_device_list.adapter = Adapter()
        rv_device_list.layoutManager = LinearLayoutManager(this)

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
                if (result?.device?.name == "C6T-128E" &&
                    !deviceList.contains(result.device)
                ) {
                    handler.post {
                        deviceList.add(result.device)
                        rv_device_list.adapter?.notifyDataSetChanged()
                        ll_loading_container.visibility = View.GONE
                        ll_device_list_container.visibility = View.VISIBLE
                    }
                }
//                if (result?.device?.name == "F1 Plus" &&
//                    !deviceList.contains(result.device)
//                ) {
//                    handler.post {
//                        deviceList.add(result.device)
//                        rv_device_list.adapter?.notifyDataSetChanged()
//                        ll_loading_container.visibility = View.GONE
//                        ll_device_list_container.visibility = View.VISIBLE
//                    }
//                }
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
                bluetoothLeScanner.startScan(mLeScanCallback)

                handler.postDelayed({
                    mScanning = false
                    bluetoothLeScanner.stopScan(mLeScanCallback)
                    deviceList.clear()
                }, 40000)

                mScanning = true
            }
            else -> {
                mScanning = false
                bluetoothLeScanner.stopScan(mLeScanCallback)
                handler.removeCallbacksAndMessages(null)
                deviceList.clear()
            }
        }
    }

    inner class Adapter : RecyclerView.Adapter<Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(
                LayoutInflater.from(this@NewSearchBraceletActivity).inflate(
                    R.layout.device_item_list,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount() = deviceList.count()

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val device = deviceList[position]
            holder.deviceName.text = device.name
            holder.deviceMacAddress.text = device.address
            holder.device = device
        }
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val deviceName: TextView = view.tv_device_name
        val deviceMacAddress: TextView = view.tv_device_mac_address
        lateinit var device: BluetoothDevice

        init {
            view.setOnClickListener {
                conectar(device)
            }
        }
    }

    fun conectar(device: BluetoothDevice) {
        ll_device_list_container.visibility = View.GONE
        ll_loading_container.visibility = View.VISIBLE
        tv_loading_message.text = getString(R.string.connecting_message)

        val intent = Intent(this, ServiceBluetoothLE::class.java)
        intent.putExtra("device", device.address)
        startService(intent)
    }

    //	@Override
    //	protected void onListItemClick(ListView l, View v, int position, long id) {
    //		final BleDevices device = mLeDeviceListAdapter.getDevice(position);
    //		if (device == null)
    //			return;
    //		final Intent intent = new Intent(this, MainActivity.class);
    //		intent.putExtra(MainActivity.EXTRAS_DEVICE_NAME, device.getName());
    //		intent.putExtra(MainActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
    //		if (mScanning) {
    //			mBLEServiceOperate.stopLeScan();
    //			mScanning = false;
    //		}
    //		startActivity(intent);
    //	}
}
