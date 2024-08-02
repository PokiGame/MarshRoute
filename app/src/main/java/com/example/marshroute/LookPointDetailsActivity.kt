package com.example.marshroute

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.marshroute.database.DatabaseManager

class LookPointDetailsActivity : AppCompatActivity() {
    private lateinit var editTextName: TextView
    private lateinit var editTextCity: TextView
    private lateinit var listViewPoints: ListView
    private lateinit var editTextAddress: TextView
    private lateinit var editTextClient: TextView
    private lateinit var editTextDescription: TextView
    private lateinit var editTextCoordinates: TextView
    private lateinit var adapter: ArrayAdapter<DatabaseManager.Point>
    private lateinit var buttonEdit: Button

    private lateinit var dbManager: DatabaseManager
    private var pointId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_look_point_details)

        editTextName = findViewById(R.id.TextPointName)
        editTextCity = findViewById(R.id.TextCityName)
        editTextAddress = findViewById(R.id.TextAddress)
        editTextClient = findViewById(R.id.TextClientName)
        editTextDescription = findViewById(R.id.TextDescription)
        editTextCoordinates = findViewById(R.id.TextCoordinates)
        buttonEdit = findViewById(R.id.buttonEdit)


        dbManager = DatabaseManager(this)
        pointId = intent.getStringExtra("point_id") ?: ""

        refreshPointsDetails()

        buttonEdit.setOnClickListener {
            val intent = Intent(this, EditPointDetailsActivity::class.java)
            intent.putExtra("point_id", pointId)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        refreshPointsDetails()
    }
    private fun refreshPointsDetails() {
        Log.d("refreshPointsDetails", "refreshPointsDetails")
        dbManager.getRoutePoint(pointId) { point ->
            point?.let {
                pointId = it.id
//                if (pointId == "0"){
//                    dbManager.getRoutePoint(pointId)
//                }

                editTextName.setText(it.name)
                editTextCity.setText(it.city)
                editTextAddress.setText(it.address)
                editTextClient.setText(it.client)
                editTextDescription.setText(it.description)
                editTextCoordinates.setText(it.coordinates)
            }
        }
    }
}
