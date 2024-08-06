package com.example.marshroute

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.marshroute.database.DatabaseManager

class EditPointDetailsActivity : AppCompatActivity() {
    private lateinit var editTextName: EditText
    private lateinit var editTextCity: EditText
    private lateinit var editTextAddress: EditText
    private lateinit var editTextClient: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextPlusCode: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonDelete: Button
    private lateinit var buttonSelectLocation: Button
    private lateinit var dbManager: DatabaseManager
    private var pointId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_point_details)

        editTextName = findViewById(R.id.editTextName)
        editTextCity = findViewById(R.id.editTextCity)
        editTextAddress = findViewById(R.id.editTextAddress)
        editTextClient = findViewById(R.id.editTextClient)
        editTextDescription = findViewById(R.id.editTextDescription)
        editTextPlusCode = findViewById(R.id.editTextCoordinates)
        buttonSave = findViewById(R.id.buttonSave)
        buttonDelete = findViewById(R.id.buttonDelete)
        buttonSelectLocation = findViewById(R.id.buttonSelectLocation)

        dbManager = DatabaseManager(this)
        pointId = intent.getStringExtra("point_id") ?: ""

        dbManager.getRoutePoint(pointId) { point ->
            point?.let {
                editTextName.setText(it.name)
                editTextCity.setText(it.city)
                editTextAddress.setText(it.address)
                editTextClient.setText(it.client)
                editTextDescription.setText(it.description)
                editTextPlusCode.setText(it.coordinates)
            }
        }

        buttonSave.setOnClickListener {
            val name = editTextName.text.toString()
            val city = editTextCity.text.toString()
            val address = editTextAddress.text.toString()
            val client = editTextClient.text.toString()
            val description = editTextDescription.text.toString()
            val pluscode = editTextPlusCode.text.toString()

            dbManager.updateRoutePoint(pointId, name, city, address, client, description, pluscode)
            Toast.makeText(this, "Точка оновлена", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
            finish()
        }

        buttonDelete.setOnClickListener {
            dbManager.deleteRoutePoint(pointId)
            Toast.makeText(this, "Точка видалена", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
            finish()
        }

        buttonSelectLocation.setOnClickListener {
            val intent = Intent(this, GeoposActivity::class.java).apply {
                putExtra("MANUAL_SELECTION", true)
            }
            startActivityForResult(intent, REQUEST_CODE_GET_LOCATION)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GET_LOCATION && resultCode == Activity.RESULT_OK) {
            val cityName = data?.getStringExtra("CITY_NAME") ?: ""
            val adminArea = data?.getStringExtra("ADMIN_AREA") ?: ""
            val plusCode = data?.getStringExtra("PLUS_CODE") ?: ""
            val addressLine = data?.getStringExtra("ADDRESS_LINE") ?: ""

            editTextCity.setText(cityName)
            editTextAddress.setText(addressLine)
            editTextPlusCode.setText(plusCode)
        }
    }

    companion object {
        private const val REQUEST_CODE_GET_LOCATION = 1
    }
}
