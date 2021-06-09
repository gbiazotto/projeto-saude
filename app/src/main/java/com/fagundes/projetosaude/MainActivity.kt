package com.fagundes.projetosaude

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fagundes.projetosaude.model.banco.Banco
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var database: Banco
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val navController by lazy { findNavController(R.id.nav_host_fragment) }
    private val appBarConfiguration by lazy {
        AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_settings
            )
        )
    }

    lateinit var handler: Handler

    private val permissions: ArrayList<String> = ArrayList()

    private var googleApiClient: GoogleApiClient? = null
    private val PLAY_SERVICES_RESOLUTION_REQUEST = 9000

    private val ALL_PERMISSIONS_RESULT = 1011
    // lists for permissions
    private var permissionsToRequest: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        database = Banco.getDataBase(this)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        handler = Handler()

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        permissionsToRequest = permissions

        val arrauAux = arrayOfNulls<String>(permissionsToRequest?.size!!)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest?.isNotEmpty()!!) {
                requestPermissions(
                    permissionsToRequest?.toArray(arrauAux)!!, ALL_PERMISSIONS_RESULT)
            }
        }

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun permissionsToRequest(wantedPermissions: ArrayList<String>): ArrayList<String>? {
        val result: ArrayList<String> = ArrayList()
        for (perm in wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm)
            }
        }
        return result
    }

    private fun hasPermission(permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    override fun onStart() {
        super.onStart()
        if (googleApiClient != null) {
            googleApiClient!!.connect()
        }
    }

    override fun onPause() {
        super.onPause()
        // stop location updates
        if (googleApiClient != null && googleApiClient!!.isConnected) {
//            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(
//                googleApiClient
//            )
            googleApiClient!!.disconnect()
        }
    }

    private fun checkPlayServices(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
            } else {
                finish()
            }
            return false
        }
        return true
    }

}
