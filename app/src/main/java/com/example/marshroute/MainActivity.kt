package com.example.marshroute

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.marshroute.database.DatabaseManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var listViewPoints: ListView
    private lateinit var dbManager: DatabaseManager
    private lateinit var adapter: ArrayAdapter<DatabaseManager.Point>
    private lateinit var buttonAddPoint: Button
    private lateinit var buttonEditPoints: Button
    private lateinit var editPointLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        val db = Firebase.firestore
        val collection = db.collection("RoutePoints")
        setContentView(R.layout.activity_main)
        dbManager = DatabaseManager(this)
        listViewPoints = findViewById(R.id.listView_points)


        buttonAddPoint = findViewById(R.id.button_add)
        buttonAddPoint.setOnClickListener {
            val intent = Intent(this, AddPointActivity::class.java)
            startActivity(intent)
        }

//        buttonEditPoints = findViewById(R.id.button_edit)
//        buttonEditPoints.setOnClickListener {
//            val intent = Intent(this, EditPointsActivity::class.java)
//            startActivity(intent)
//        }

        editPointLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                refreshPointsList()
            }
        }
        refreshPointsList()

        listViewPoints.setOnItemClickListener { _, _, position, _ ->
            val selectedPoint = adapter.getItem(position)
            Log.d("EditPointsActivity", selectedPoint?.toString() ?: "No Point Selected") // Оновлено
            selectedPoint?.let {
                val intent = Intent(this, LookPointDetailsActivity::class.java)
                intent.putExtra("point_id", it.id)
                editPointLauncher.launch(intent)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        refreshPointsList()
    }
    private fun refreshPointsList() {
        dbManager.getAllRoutePoints { points ->
            // Оновлюємо адаптер
            adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, points)
            listViewPoints.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }
}
