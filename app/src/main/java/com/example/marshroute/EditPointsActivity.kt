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

class EditPointsActivity : AppCompatActivity() {

    private lateinit var listViewPoints: ListView
    private lateinit var dbManager: DatabaseManager
    private lateinit var adapter: ArrayAdapter<Point> // Змінено на ArrayAdapter<Point>
    private lateinit var editPointLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_points)

        listViewPoints = findViewById(R.id.listView_points)
        dbManager = DatabaseManager(this)

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
        // Отримуємо всі точки з dbManager
        val dbPoints = dbManager.getAllRoutePoints()

        // Конвертуємо DatabaseManager.Point у ваш Point
        val points = dbPoints.map {
            Point(
                id = it.id,
                name = it.name,
                address = it.address,
                description = it.description
            )
        }

        // Оновлюємо адаптер
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, points)
        listViewPoints.adapter = adapter
        adapter.notifyDataSetChanged()
    }


    companion object {
        private const val REQUEST_EDIT_POINT = 1
    }
}
