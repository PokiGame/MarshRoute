package com.example.marshroute

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.example.marshroute.database.DatabaseManager

class SelectPointsActivity : AppCompatActivity() {
    private lateinit var editPointLauncher: ActivityResultLauncher<Intent>
    private lateinit var dbManager: DatabaseManager
    private lateinit var listViewPoints: ListView
    private lateinit var adapter: ArrayAdapter<DatabaseManager.Point>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_points)

        listViewPoints = findViewById(R.id.listView_points)
        dbManager = DatabaseManager(this)

        dbManager.getAllRoutePoints { points ->
            adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, points)
            listViewPoints.adapter = adapter
        }

        listViewPoints.setOnItemClickListener { _, _, position, _ ->
            val selectedPoint = adapter.getItem(position)
            selectedPoint?.let {
                val intent = Intent(this, EditPointDetailsActivity::class.java)
                intent.putExtra("point_id", it.id)
                startActivity(intent)
            }
        }
    }
}
