package com.example.marshroute

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.openlocationcode.OpenLocationCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

class GeoposActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var manualSelection = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geopos_get)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        manualSelection = intent.getBooleanExtra("MANUAL_SELECTION", false)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            getLocationAndUpdateUI()
        }

        if (manualSelection) {
            map.setOnMapClickListener { latLng ->
                map.clear()
                map.addMarker(MarkerOptions().position(latLng).title("Вибрана точка"))
                getPlusCodeAndAddress(latLng)
            }
        }
    }

    private fun getLocationAndUpdateUI() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permission not granted")
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                Log.d(TAG, "Location obtained: $currentLatLng")
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                if (!manualSelection) {
                    getPlusCodeAndAddress(currentLatLng)
                }
            } ?: run {
                Log.e(TAG, "Location is null")
            }
        }
    }

    private fun getPlusCode(latLng: LatLng): String {
        return OpenLocationCode.encode(latLng.latitude, latLng.longitude)
    }

    private fun getPlusCodeAndAddress(latLng: LatLng) {
        if (isNetworkAvailable()) {
            GlobalScope.launch {
                val geocoder = Geocoder(this@GeoposActivity, Locale("uk", "UA"))
                val addressList = withContext(Dispatchers.IO) {
                    try {
                        geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    } catch (e: IOException) {
                        Log.e(TAG, "IOException: ${e.message}")
                        null
                    } catch (e: SecurityException) {
                        Log.e(TAG, "SecurityException: ${e.message}")
                        null
                    }
                }

                val address = addressList?.firstOrNull()
                val plusCode = getPlusCode(latLng)
                val addressLine = address?.getAddressLine(0) ?: ""
                val locality = address?.locality ?: ""
                val adminArea = address?.adminArea ?: ""

                // Форматування адреси
                val formattedPlusCode = buildString {
                    append(plusCode) // Додаємо Plus Code
                    if (locality.isNotEmpty()) {
                        append(" ")
                        append(locality) // Додаємо населений пункт
                    }
                    if (adminArea.isNotEmpty()) {
                        append(", ")
                        append(adminArea) // Додаємо область
                    }
                }
                val formattedAddressLine = buildString {
                    val tempAddressArray = addressLine.split(", ")
                    append(tempAddressArray[0])
                    if (tempAddressArray.size > 1 && tempAddressArray[1].all { it.isDigit() }){
                        append(", " + tempAddressArray[1])
                    }
                }

                val resultIntent = Intent().apply {
                    putExtra("CITY_NAME", locality)
                    putExtra("ADMIN_AREA", adminArea)
                    putExtra("PLUS_CODE", formattedPlusCode) // Використовуємо форматований Plus Code
                    putExtra("ADDRESS_LINE", formattedAddressLine)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        } else {
            showToast("No Internet connection")
            finish()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities != null &&
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndUpdateUI()
            } else {
                Log.e(TAG, "Location permission denied")
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val TAG = "GeoposActivity"
    }
}
