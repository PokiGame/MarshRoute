package com.example.marshroute

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.marshroute.database.DatabaseManager

class AddPointActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextCity: EditText
    private lateinit var editTextAddress: EditText
    private lateinit var editTextClient: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextCoordinates: EditText
    private lateinit var buttonSave: Button
    private lateinit var dbManager: DatabaseManager

    private val GEO_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_point)

        editTextName = findViewById(R.id.editTextName)
        editTextCity = findViewById(R.id.editTextCity)
        editTextAddress = findViewById(R.id.editTextAddress)
        editTextClient = findViewById(R.id.editTextClient)
        editTextDescription = findViewById(R.id.editTextDescription)
        editTextCoordinates = findViewById(R.id.editTextCoordinates)
        buttonSave = findViewById(R.id.buttonSave)
        dbManager = DatabaseManager(this)

        // Запускаємо GeoposActivity автоматично при завантаженні AddPointActivity
        startGeoposActivityForResult()

        buttonSave.setOnClickListener {
            val name = editTextName.text.toString()
            val city = editTextCity.text.toString()
            val address = editTextAddress.text.toString()
            val client = editTextClient.text.toString()
            val description = editTextDescription.text.toString()
            val coordinates = editTextCoordinates.text.toString()

            if (client.isNotEmpty() && coordinates.isNotEmpty() && city.isNotEmpty()) {
                dbManager.addRoutePoint(name, city, address, client, description, coordinates)
                Toast.makeText(this, "Точка додана", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Клієнт, місто та Plus Code обов'язково!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startGeoposActivityForResult() {
        val intent = Intent(this, GeoposActivity::class.java)
        startActivityForResult(intent, GEO_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GEO_REQUEST_CODE && resultCode == RESULT_OK) {
            val cityName = data?.getStringExtra("CITY_NAME") ?: "Not available"
            val plusCode = data?.getStringExtra("PLUS_CODE") ?: "Not available"
            val addressLine = data?.getStringExtra("ADDRESS_LINE") ?: "Not available"

            editTextCity.setText(cityName)
            editTextCoordinates.setText(plusCode)
            editTextAddress.setText(addressLine)
        }
    }
}
