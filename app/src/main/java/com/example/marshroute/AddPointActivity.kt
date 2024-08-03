package com.example.marshroute

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
                Toast.makeText(this, "Клієнт, місто та координати обовязково!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
