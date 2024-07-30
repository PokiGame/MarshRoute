package com.example.marshroute

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddPointActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextAddress: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var buttonSave: Button
    private lateinit var dbManager: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_point)

        editTextName = findViewById(R.id.editTextName)
        editTextAddress = findViewById(R.id.editTextAddress)
        editTextDescription = findViewById(R.id.editTextDescription)
        buttonSave = findViewById(R.id.buttonSave)
        dbManager = DatabaseManager(this)

        buttonSave.setOnClickListener {
            val name = editTextName.text.toString()
            val address = editTextAddress.text.toString()
            val description = editTextDescription.text.toString()

            if (name.isNotEmpty() && address.isNotEmpty()) {
                dbManager.addRoutePoint(name, address, description)
                Toast.makeText(this, "Point added", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Name and address are required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
