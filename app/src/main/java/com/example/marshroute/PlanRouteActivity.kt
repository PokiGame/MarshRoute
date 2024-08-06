package com.example.marshroute

import GeocodingApi
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.marshroute.database.DatabaseManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class PlanRouteActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var dbManager: DatabaseManager

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/maps/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val geocodingApi = retrofit.create(GeocodingApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_route)

        dbManager = DatabaseManager(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val buttonSelectPoints: Button = findViewById(R.id.button_select_points)
        buttonSelectPoints.setOnClickListener {
            Log.d("PlanRouteActivity", "Button 'Select Points' clicked")
            getRoutePoints()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        Log.d("PlanRouteActivity", "Google Map is ready")
    }

    private fun getRoutePoints() {
        Log.d("PlanRouteActivity", "Fetching route points from database")
        dbManager.getAllRoutePoints { points ->
            if (points.isNotEmpty()) {
                Log.d("PlanRouteActivity", "Points found: ${points.size}")
                CoroutineScope(Dispatchers.IO).launch {
                    val waypoints = mutableListOf<LatLng>()
                    for (point in points) {
                        try {
                            Log.d("PlanRouteActivity", "Processing point: ${point.name}")
                            val coordinates = getLatLngFromPlusCode(point.coordinates)
                            if (coordinates != null) {
                                waypoints.add(coordinates)
                                withContext(Dispatchers.Main) {
                                    googleMap.addMarker(MarkerOptions().position(coordinates).title(point.name))
                                    Log.d("PlanRouteActivity", "Marker added for point: ${point.name} at ${coordinates.latitude}, ${coordinates.longitude}")
                                }
                            } else {
                                Log.e("PlanRouteActivity", "Invalid coordinates for point: ${point.name}")
                            }
                        } catch (e: Exception) {
                            Log.e("PlanRouteActivity", "Error processing point: ${point.name}", e)
                        }
                    }

                    if (waypoints.size > 1) {
                        val origin = waypoints.first()
                        val destination = waypoints.last()
                        val waypointsString = waypoints.drop(1).dropLast(1).joinToString("|") { "${it.latitude},${it.longitude}" }

                        Log.d("PlanRouteActivity", "Calculating route from $origin to $destination with waypoints: $waypointsString")

                        try {
                            val response = RetrofitClient.apiService.getDirections(
                                origin = "${origin.latitude},${origin.longitude}",
                                destination = "${destination.latitude},${destination.longitude}",
                                waypoints = waypointsString,
                                apiKey = getString(R.string.google_maps_key) // Отримання ключа з ресурсів
                            )

                            if (response.isSuccessful) {
                                Log.d("PlanRouteActivity", "Route fetched successfully")
                                val directionsResponse = response.body()
                                val polylineOptions = PolylineOptions().apply {
                                    addAll(directionsResponse?.routes?.flatMap { route ->
                                        route.legs.flatMap { leg ->
                                            leg.steps.flatMap { step ->
                                                listOf(
                                                    LatLng(step.start_location.lat, step.start_location.lng),
                                                    LatLng(step.end_location.lat, step.end_location.lng)
                                                )
                                            }
                                        }
                                    } ?: emptyList())
                                    color(android.graphics.Color.BLUE)
                                    width(5f)
                                }
                                withContext(Dispatchers.Main) {
                                    googleMap.addPolyline(polylineOptions)
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 10f))
                                    Log.d("PlanRouteActivity", "Polyline added to map and camera moved to origin")
                                }
                            } else {
                                Log.e("PlanRouteActivity", "Error fetching route: ${response.message()}")
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@PlanRouteActivity, "Помилка при отриманні даних маршруту", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("PlanRouteActivity", "Exception occurred: ${e.message}", e)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@PlanRouteActivity, "Помилка при запиті", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Log.d("PlanRouteActivity", "Не достатньо точок для прокладання маршруту")
                    }
                }
            } else {
                Log.d("PlanRouteActivity", "No points found in database")
                Toast.makeText(this, "Точки не знайдені", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private suspend fun getLatLngFromPlusCode(plusCode: String): LatLng? {
        // Усуваємо можливі пробіли і символи

        val cleanedPlusCode = plusCode.trim().replace(" ", "+")
        testGeocodingApi(cleanedPlusCode)
        Log.d("PlanRouteActivity", "Requesting coordinates for Plus Code: $cleanedPlusCode")
        return try {
            val response = geocodingApi.getCoordinatesFromPlusCode(cleanedPlusCode, getString(R.string.google_maps_key))
            if (response.results.isNotEmpty()) {
                val location = response.results[0].geometry.location
                LatLng(location.lat, location.lng)
            } else {
                Log.e("PlanRouteActivity", "No results found for Plus Code: $cleanedPlusCode")
                null
            }
        } catch (e: Exception) {
            Log.e("PlanRouteActivity", "Error getting coordinates from Plus Code", e)
            null
        }
    }

    private suspend fun testGeocodingApi(plusCode: String): Unit {
        val response = geocodingApi.getCoordinatesFromPlusCode(plusCode, getString(R.string.google_maps_key))
        Log.d("PlanRouteActivity", "Response: $response")
    }





}
