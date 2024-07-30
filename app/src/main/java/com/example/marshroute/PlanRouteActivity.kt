package com.example.marshroute

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity



class PlanRouteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_route)

        val buttonSelectPoints: Button = findViewById(R.id.button_select_points)

        buttonSelectPoints.setOnClickListener {
            // Тут має бути реалізація для вибору точок та прокладання маршруту
            Toast.makeText(this, "Вибір точок та прокладання маршруту", Toast.LENGTH_LONG).show()
        }
    }
}
