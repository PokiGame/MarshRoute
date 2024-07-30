package com.example.marshroute

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditPointDetailsActivity : AppCompatActivity() {

    private lateinit var dbManager: DatabaseManager
    private lateinit var editTextName: EditText
    private lateinit var editTextAddress: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonDelete: Button
    private var pointId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_point_details)

        dbManager = DatabaseManager(this)

        editTextName = findViewById(R.id.editTextName)
        editTextAddress = findViewById(R.id.editTextAddress)
        editTextDescription = findViewById(R.id.editTextDescription)
        buttonSave = findViewById(R.id.buttonSave)
        buttonDelete = findViewById(R.id.buttonDelete)

        pointId = intent.getLongExtra("point_id", -1)

        val point = dbManager.getRoutePointById(pointId)
        point?.let {
            editTextName.setText(it.name)
            editTextAddress.setText(it.address)
            editTextDescription.setText(it.description)
        }

        buttonSave.setOnClickListener {
            val name = editTextName.text.toString()
            val address = editTextAddress.text.toString()
            val description = editTextDescription.text.toString()

            dbManager.updateRoutePoint(pointId, name, address, description)
            Toast.makeText(this, "Point updated", Toast.LENGTH_SHORT).show()
            finish()
        }
        buttonDelete.setOnClickListener {
            val deletedRows = dbManager.deleteRoutePoint(pointId)
            if (deletedRows > 0) {
                Toast.makeText(this, "Точка видалена", Toast.LENGTH_SHORT).show()
                finish()  // Повертаємось до попередньої активності
            } else {
                Toast.makeText(this, "Помилка при видаленні", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
