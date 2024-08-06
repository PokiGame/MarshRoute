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
    private lateinit var editTextPlusCode: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonSetPointManually: Button
    private lateinit var dbManager: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_point)

        editTextName = findViewById(R.id.editTextName)
        editTextCity = findViewById(R.id.editTextCity)
        editTextAddress = findViewById(R.id.editTextAddress)
        editTextClient = findViewById(R.id.editTextClient)
        editTextDescription = findViewById(R.id.editTextDescription)
        editTextPlusCode = findViewById(R.id.editTextCoordinates)
        buttonSave = findViewById(R.id.buttonSave)
        buttonSetPointManually = findViewById(R.id.buttonSetPointManually)
        dbManager = DatabaseManager(this)

        buttonSave.setOnClickListener {
            val name = editTextName.text.toString()
            val city = editTextCity.text.toString()
            val address = editTextAddress.text.toString()
            val client = editTextClient.text.toString()
            val description = editTextDescription.text.toString()
            val coordinates = editTextPlusCode.text.toString()

            if (client.isNotEmpty() && coordinates.isNotEmpty() && city.isNotEmpty()) {
                dbManager.addRoutePoint(name, city, address, client, description, coordinates)
                Toast.makeText(this, "Точка додана", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Клієнт, місто та Plus Code обов'язково!", Toast.LENGTH_SHORT).show()
            }
        }

        buttonSetPointManually.setOnClickListener {
            // Запускаємо GeoposActivity для ручного встановлення точки
            val intent = Intent(this, GeoposActivity::class.java)
            intent.putExtra("MANUAL_SELECTION", true)
            startActivityForResult(intent, REQUEST_CODE_GET_LOCATION)
        }

        // Запускаємо GeoposActivity для автоматичного отримання даних про точку
        val intent = Intent(this, GeoposActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_GET_LOCATION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GET_LOCATION && resultCode == RESULT_OK) {
            val cityName = data?.getStringExtra("CITY_NAME") ?: ""
            val adminArea = data?.getStringExtra("ADMIN_AREA") ?: ""
            val plusCode = data?.getStringExtra("PLUS_CODE") ?: ""
            val addressTemp = data?.getStringExtra("ADDRESS_LINE") ?: ""
            //val addressLine: String
            if (addressTemp != "Вулиця без назви"){
                editTextAddress.setText(addressTemp)
            }


            editTextCity.setText(cityName)
            //editTextAddress.setText(addressLine)
            editTextPlusCode.setText(plusCode)
        }
    }

    companion object {
        private const val REQUEST_CODE_GET_LOCATION = 1
    }
}
