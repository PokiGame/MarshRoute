package com.example.marshroute

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class DatabaseHelper(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()

    fun addPoint(name: String, address: String, description: String, onComplete: (Boolean) -> Unit) {
        val point = hashMapOf(
            "name" to name,
            "address" to address,
            "description" to description
        )
        db.collection("points")
            .add(point)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    fun getPoints(onComplete: (List<Point>) -> Unit) {
        db.collection("points")
            .get()
            .addOnSuccessListener { result ->
                val points = result.mapNotNull { document ->
                    document.toObject(Point::class.java).apply {
                        id = document.id
                    }
                }
                onComplete(points)
            }
            .addOnFailureListener {
                onComplete(emptyList())
            }
    }

    fun updatePoint(id: String, name: String, address: String, description: String, onComplete: (Boolean) -> Unit) {
        val point = hashMapOf(
            "name" to name,
            "address" to address,
            "description" to description
        )
        db.collection("points").document(id)
            .set(point)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    fun deletePoint(id: String, onComplete: (Boolean) -> Unit) {
        db.collection("points").document(id)
            .delete()
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    data class Point(
        var id: String = "",
        val name: String = "",
        val city: String = "",
        val address: String = "",
        val client: String = "",
        val description: String = ""
    ) {
        override fun toString(): String {
            var clienttemp: String = client
            var descriptiontemp: String = description
            if (client.isNotEmpty()){ clienttemp = ", " + client }
            if (description.isNotEmpty()){ descriptiontemp = ", " + description }
            Log.d("EditPointDetailsActivity", client.toString())
            Log.d("EditPointDetailsActivity", description.toString())
            return "$name, $city, $address$clienttemp$descriptiontemp"
        }
    }

}
