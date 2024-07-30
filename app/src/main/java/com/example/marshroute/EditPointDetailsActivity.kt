package com.example.marshroute

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.os.HandlerThread
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditPointDetailsActivity : AppCompatActivity() {
    private lateinit var handler: Handler
    private lateinit var editTextName: EditText
    private lateinit var editTextCity: EditText
    private lateinit var editTextAddress: EditText
    private lateinit var editTextClient: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonDelete: Button
    private lateinit var dbManager: DatabaseManager
    private var pointId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_point_details)

        editTextName = findViewById(R.id.editTextName)
        editTextCity = findViewById(R.id.editTextCity)
        editTextAddress = findViewById(R.id.editTextAddress)
        editTextClient = findViewById(R.id.editTextClient)
        editTextDescription = findViewById(R.id.editTextDescription)
        buttonSave = findViewById(R.id.buttonSave)
        buttonDelete = findViewById(R.id.buttonDelete)

        dbManager = DatabaseManager(this)
        pointId = intent.getLongExtra("point_id", -1)

        val handlerThread = HandlerThread("MyHandlerThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        val point = dbManager.getRoutePoint(pointId)
        point?.let {
            editTextName.setText(it.name)
            editTextCity.setText(it.city)
            editTextAddress.setText(it.address)
            editTextClient.setText(it.client)
            editTextDescription.setText(it.description)
        }

        buttonSave.setOnClickListener {
            val name = editTextName.text.toString()
            val city = editTextCity.text.toString()
            val address = editTextAddress.text.toString()
            val client = editTextClient.text.toString()
            val description = editTextDescription.text.toString()

            val updatedRows = dbManager.updateRoutePoint(pointId, name, city, address, client, description)
            if (updatedRows > 0) {
                Toast.makeText(this, "Точка оновлена", Toast.LENGTH_SHORT).show()
                val resultIntent = Intent()
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Помилка при оновленні", Toast.LENGTH_SHORT).show()
            }
        }

        handler.postDelayed({
            buttonDelete.isEnabled = true
        }, 5000)

        buttonDelete.setOnClickListener {
            val deletedRows = dbManager.deleteRoutePoint(pointId)
            if (deletedRows > 0) {
                Toast.makeText(this, "Точка видалена", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Помилка при видаленні", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
