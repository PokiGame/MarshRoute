package com.example.marshroute

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class EditPointsActivity : AppCompatActivity() {

    private lateinit var listViewPoints: ListView
    private lateinit var dbManager: DatabaseManager
    private lateinit var adapter: ArrayAdapter<DatabaseManager.Point> // Змінено на ArrayAdapter<DatabaseManager.Point>
    private lateinit var editPointLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_points)

        dbManager = DatabaseManager(this)
        listViewPoints = findViewById(R.id.listView_points)

        // Ініціалізуємо editPointLauncher
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
                val intent = Intent(this, EditPointDetailsActivity::class.java)
                intent.putExtra("point_id", it.id)
                editPointLauncher.launch(intent)
            }
        }
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
