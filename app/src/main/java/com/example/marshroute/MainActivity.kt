package com.example.marshroute

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private lateinit var buttonAddPoint: Button
    private lateinit var buttonEditPoints: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)

        buttonAddPoint = findViewById(R.id.button_add)
        buttonEditPoints = findViewById(R.id.button_edit)

        buttonAddPoint.setOnClickListener {
            val intent = Intent(this, AddPointActivity::class.java)
            startActivity(intent)
        }

        buttonEditPoints.setOnClickListener {
            val intent = Intent(this, EditPointsActivity::class.java)
            startActivity(intent)
        }
    }
}
