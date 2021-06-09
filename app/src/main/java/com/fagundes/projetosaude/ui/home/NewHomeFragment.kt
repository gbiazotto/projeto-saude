package com.fagundes.projetosaude.ui.home

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fagundes.projetosaude.MainActivity
import com.fagundes.projetosaude.R
import com.fagundes.projetosaude.model.*
import com.fagundes.projetosaude.rede.RetrofitInitializer
import com.fagundes.projetosaude.services.StatusMedicao
import com.fagundes.projetosaude.ui.NewSearchBraceletActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yc.pedometer.info.RateOneDayInfo
import com.yc.pedometer.sdk.UTESQLOperate
import com.yc.pedometer.sdk.WriteCommandToBLE
import com.yc.pedometer.utils.CalendarUtils
import com.yc.pedometer.utils.GlobalVariable
import kotlinx.android.synthetic.main.fragment_new_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class NewHomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val mWriteCommand: WriteCommandToBLE? = null
    private val UPDATA_REAL_RATE_MSG = 20
    private val tempRate = 70
    private val tempStatus = 0
    private var mySQLOperate: UTESQLOperate? = null

    private val mHandler = Handler() { message ->
        when (message.what) {
            UPDATA_REAL_RATE_MSG -> {
                tv_heart_frequency.text = tempRate.toString() + ""
                if (tempStatus == GlobalVariable.RATE_TEST_FINISH) {
                    updateUpdataRateMainUI(CalendarUtils.getCalendar(0));
                }
                true
            }
            else -> {
                false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_home, container, false)

        val activity: MainActivity? = (activity as MainActivity)

        homeViewModel =
            ViewModelProviders.of(
                this,
                MyViewModelFactory(
                    activity!!
                )
            ).get(HomeViewModel::class.java)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        mySQLOperate = UTESQLOperate.getInstance(requireContext())

        setObservables()

        return view
    }

    private fun setObservables() {
        StatusMedicao.getStatusMedicao().conexao
            .observe(this, Observer {
                if (it) {
                    tv_bracelet_status.text =
                        getString(R.string.bracelet_status_pattern, "Conectado")
                    btn_search_bracelet.isEnabled = false
                    btn_init_reading.isEnabled = true
                }
            })

        homeViewModel.frequencia.observe(this, Observer {
            if (it != null) {
                pb_heart_frequency_loading.visibility = View.GONE
                tv_heart_frequency.visibility = View.VISIBLE
                tv_heart_frequency.text =
                    getString(R.string.heart_frequency_pattern, it.frequencia.toString())
                registerData(it.frequencia, null)
            }
        })

        homeViewModel.pressao.observe(this, Observer {
            if (it != null) {
                pb_blood_pressure_loading.visibility = View.GONE
                tv_blood_pressure.visibility = View.VISIBLE
                tv_blood_pressure.text = getString(
                    R.string.blood_pressure_pattern,
                    it.alta.toString(),
                    it.baixa.toString()
                )
                registerData(null, BloodPressure(it.alta, it.baixa))
            }
        })

        homeViewModel.oxygenio.observe(this, Observer {
            if (it != null) {
                pb_oxygen_loading.visibility = View.GONE
                tv_oxygen.visibility = View.VISIBLE
                tv_oxygen.text = getString(R.string.oxygen_pattern, it.valor.toString())
            }
        })
    }

    private fun registerData(heartFrequency: Int?, bloodPressure: BloodPressure?) {
        val imei = getSharedPrefs()?.getString(getString(R.string.USER_UUID_KEY), "")
        val newDataReadBody = NewDataReadBody(imei!!)

        heartFrequency?.let { newDataReadBody.heartHate = it }
        bloodPressure?.let { newDataReadBody.bloodPressure = it }

        val api = RetrofitInitializer().getAPIs()
        val call = api.registerNewDataRead(newDataReadBody)

        call.enqueue(object : Callback<Any?> {
            override fun onFailure(call: Call<Any?>, t: Throwable) {
                showToast("Dados não registrados!")
            }

            override fun onResponse(
                call: Call<Any?>,
                response: Response<Any?>
            ) {
                if (response.isSuccessful) {
                    showToast("Dados registrados!")
                }
            }

        })
    }

    override fun onStart() {
        super.onStart()
        getUserUuid()
        setListeners()

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                registerLocation(location)
            }
    }

    private fun getUserUuid() {
        val userUuid = getSharedPrefs()?.getString(getString(R.string.USER_UUID_KEY), null)

        if (userUuid.isNullOrEmpty()) {
            createUserUuid()
        } else {
            setUserOnScreen(userUuid)
        }
    }

    private fun setListeners() {
        btn_search_bracelet.setOnClickListener {
            activity?.startActivity(Intent(activity, NewSearchBraceletActivity::class.java))
        }

        btn_init_reading.setOnClickListener {
            tv_heart_frequency.visibility = View.GONE
            pb_heart_frequency_loading.visibility = View.VISIBLE

            tv_blood_pressure.visibility = View.GONE
            pb_blood_pressure_loading.visibility = View.VISIBLE

            tv_oxygen.visibility = View.GONE
            pb_oxygen_loading.visibility = View.VISIBLE

//            StatusMedicao.getStatusMedicao().commandosBluetooth?.connectou(true)
            mWriteCommand?.sendRateTestCommand(GlobalVariable.RATE_TEST_START)
        }
    }

    private fun getSharedPrefs() = activity?.getSharedPreferences(
        getString(R.string.SHARED_PREFS_KEY),
        Context.MODE_PRIVATE
    )

    private fun createUserUuid() {
        val userUuid = UUID.randomUUID().toString()
        registerUserDevice(userUuid)
        setUserOnScreen(userUuid)
    }

    private fun setUserOnScreen(userUuid: String) {
        tv_user_id.text = getString(R.string.user_uuid_pattern, userUuid)
    }

    private fun registerUserDevice(userUuid: String) {
        val api = RetrofitInitializer().getAPIs()
        val call = api.registerNewDevice(NewDeviceBody(userUuid))

        call.enqueue(object : Callback<Response<Any?>?> {
            override fun onFailure(call: Call<Response<Any?>?>, t: Throwable) {
                Log.i("Registro", t.message!!)
                showToast("Usuário não registrado!")
            }

            override fun onResponse(
                call: Call<Response<Any?>?>,
                response: Response<Response<Any?>?>
            ) {
                if (response.isSuccessful) {
                    showToast("Usuário registrado!")
                    saveUserUuid(userUuid)
                }
            }

        })
    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveUserUuid(userUuid: String) {
        with(getSharedPrefs()?.edit()!!) {
            putString(getString(R.string.USER_UUID_KEY), userUuid)
            commit()
        }
    }

    private fun registerLocation(location: Location?) {
        val userUuid = getSharedPrefs()?.getString(getString(R.string.USER_UUID_KEY), null)
        val newLocationBody =
            NewLocationBody(
                userUuid!!,
                GPS(location?.latitude.toString(), location?.longitude.toString())
            )
        val api = RetrofitInitializer().getAPIs()
        val call = api.registerNewLocation(newLocationBody)

        call.enqueue(object : Callback<Any?> {
            override fun onFailure(call: Call<Any?>, t: Throwable) {
                showToast("Localização não registrada!")
            }

            override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                if (response.isSuccessful) {
                    showToast("Localização registrada!")
                }
            }

        })
    }

    private fun updateUpdataRateMainUI(calendar: String) {
        // UTESQLOperate mySQLOperate = UTESQLOperate.getInstance(mContext);
        val mRateOneDayInfo: RateOneDayInfo = mySQLOperate?.queryRateOneDayMainInfo(calendar)!!
        if (mRateOneDayInfo != null) {
            val currentRate = mRateOneDayInfo.currentRate
            val lowestValue = mRateOneDayInfo.lowestRate
            val averageValue = mRateOneDayInfo.verageRate
            val highestValue = mRateOneDayInfo.highestRate
            // current_rate.setText(currentRate + "");
            if (currentRate == 0) {
                tv_heart_frequency.text = "--"
            } else {
                tv_heart_frequency.text = currentRate.toString() + ""
            }
//            if (lowestValue == 0) {
//                tv_lowest_rate.setText("--")
//            } else {
//                tv_lowest_rate.setText(lowestValue.toString() + "")
//            }
//            if (averageValue == 0) {
//                tv_verage_rate.setText("--")
//            } else {
//                tv_verage_rate.setText(averageValue.toString() + "")
//            }
//            if (highestValue == 0) {
//                tv_highest_rate.setText("--")
//            } else {
//                tv_highest_rate.setText(highestValue.toString() + "")
//            }
        } else {
            tv_heart_frequency.text = "--"
        }
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