package com.fagundes.projetosaude.services

import android.app.*
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.fagundes.projetosaude.MainActivity
import com.fagundes.projetosaude.NewDataHandlerUtil
import com.fagundes.projetosaude.R
import com.fagundes.projetosaude.model.Repositorio
import com.fagundes.projetosaude.model.banco.Banco
import com.fagundes.projetosaude.utils.BuildDTOs
import com.google.android.gms.location.LocationServices
import java.util.*

class ServiceBluetoothLE : Service() {

    private val bluetoothAdapter: BluetoothAdapter by lazy { bluetoothManager.adapter }
    private val bluetoothManager by lazy { getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager }
    private var banco: Banco? = null
    lateinit var commands: CommandosBluetooth
    lateinit var handler: Handler

    private fun getNotification(): Notification {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this, MainActivity::class.java)

        val pedingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW

            val channel =
                NotificationChannel(CHANNEL_1D, "Leitura ativada", importance)

            channel.description = "Leitura"
            channel.enableLights(false)
            notificationManager.createNotificationChannel(channel)

            Notification.Builder(this, CHANNEL_1D)
                .setContentTitle("Zetta")
                .setContentText("Leitura do relógio esta ativada")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setChannelId(CHANNEL_1D)
                .setContentIntent(pedingIntent)
                .build()
        } else {
            val notification = NotificationCompat.Builder(this)
                .setCategory(Notification.DEFAULT_ALL.toString())
                .setContentTitle("Zetta")
                .setContentText("Leitura do relógio esta ativada")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pedingIntent)
                .setAutoCancel(true)

            notification.build()
        }
    }

    override fun onStart(intent: Intent?, startId: Int) {
        val mac = intent?.extras?.get("device")

        Log.i("servico", "onStartCommand $mac")

        if (mac != null) {
            connect(mac.toString())
        } else {
            stopSelf()
        }
    }

    override fun onBind(intent: Intent?): Nothing? = null

    override fun onCreate() {
        super.onCreate()

        Log.i("servico", "Serviço criado")

        startForeground(157, getNotification())

        handler = Handler()

        if (!bluetoothAdapter.isEnabled) {
            bluetoothAdapter.enable()
        }

        banco = Banco.getDataBase(this)

        commands =
            CommandosBluetooth(
                this,
                LocationServices.getFusedLocationProviderClient(this),
                Repositorio.getRepositorio(
                    this
                )
            )
    }

    private lateinit var mBluetoothGatt: BluetoothGatt

    private val mGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(
            gatt: BluetoothGatt,
            status: Int,
            newState: Int
        ) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this@ServiceBluetoothLE, "Relógio Conectado", Toast.LENGTH_SHORT)
                        .show()
                }

                StatusMedicao.getStatusMedicao()
                    .conexao.postValue(true)

                val intent = Intent("bluetooth.ActivityBusca.conectou")

                sendBroadcast(intent)

                mBluetoothGatt.discoverServices()

                Log.i("bluetooth", "Conectado")
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("bluetooth", "Desconectado")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Log.i("bluetooth", "serviçoes descobertos")

            val rxService = mBluetoothGatt.getService(RX_SERVICE_UUID) ?: return

            val txChar = rxService.getCharacteristic(TX_CHAR_UUID) ?: return

            mBluetoothGatt.setCharacteristicNotification(txChar, true)

            val descriptor = txChar.getDescriptor(CCCD)
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            mBluetoothGatt.writeDescriptor(descriptor)

            handler.postDelayed({
                commands.connectou(true)
            }, 1000)
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            Log.i("bluetooth", "Characteristicas lidas ")
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            Log.i("bluetooth", "Characteristicas alteradas ${characteristic.value}")

            val datas = NewDataHandlerUtil().bytesToArrayList(characteristic.value)

            commands.novosDados(datas as MutableList<Int>)
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            Log.i("bluetooth", "Characteristicas Enviadas $status ${characteristic.value}")
            super.onCharacteristicWrite(gatt, characteristic, status)
        }
    }

    fun writeRXCharacteristic(value: ByteArray?): Boolean {
        Log.i("leitura", "Leitura writeRXCharacteristic iniciada - 0")

        val rxService = mBluetoothGatt.getService(RX_SERVICE_UUID) ?: return false

        Log.i("leitura", "Leitura writeRXCharacteristic iniciada - 1")

        val rxChar = rxService.getCharacteristic(RX_CHAR_UUID) ?: return false

        Log.i("leitura", "Leitura writeRXCharacteristic iniciada - 2")

        rxChar.value = value
        val status = mBluetoothGatt.writeCharacteristic(rxChar)

        Log.i("leitura", "Leitura writeRXCharacteristic iniciada - $status")

        return status
    }

    private fun connect(mac: String) {
        Log.i("servico", "connect $mac")
        val device = bluetoothAdapter.getRemoteDevice(mac)
        mBluetoothGatt = device.connectGatt(this@ServiceBluetoothLE, true, mGattCallback)
        mBluetoothGatt.connect()
    }

    fun iniciaEnvioDeDados() {
        handler.postDelayed({
            Log.i("leitura", "no handler oara reiniciar")
            commands.connectou(true)
        }, 5 * 60 * 1000)

        if (banco == null) return

        BuildDTOs(this, banco!!).getDTOTransmissao().observeForever { dto ->
            Log.i("dto", "dto recebido $dto")

            Handler(Looper.getMainLooper()).post {
                Toast.makeText(this, "Dados enviados para o Firebase! ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val CHANNEL_1D = "br.com.saude"

        val CCCD: UUID = UUID
            .fromString("00002902-0000-1000-8000-00805f9b34fb")
        val RX_SERVICE_UUID: UUID = UUID
            .fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
        val RX_CHAR_UUID: UUID = UUID
            .fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")
        val TX_CHAR_UUID: UUID = UUID
            .fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")
    }
}
