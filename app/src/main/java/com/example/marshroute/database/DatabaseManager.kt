package com.example.marshroute.database

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DatabaseManager(context: Context) {
    val db = Firebase.firestore
    val collection = db.collection("RoutePoints")

    companion object {
        private const val TAG = "DatabaseManager" //hyi
    }

    // Додати нову точку маршруту
    fun addRoutePoint(name: String, city: String, address: String, client: String, description: String) {
        Log.d(TAG, "addRoutePoint")
        val point = hashMapOf(
            "name" to name,
            "city" to city,
            "address" to address,
            "client" to client,
            "description" to description
        )
        collection.add(point)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    // Отримати всі точки маршруту
    fun getAllRoutePoints(onResult: (List<Point>) -> Unit) {
        Log.d(TAG, "getAllRoutePoints")
        collection.get()
            .addOnSuccessListener { result ->
                val pointsList = mutableListOf<Point>()
                for (document in result) {
                    val point = document.toObject(Point::class.java).copy(id = document.id)
                    pointsList.add(point)
                }
                onResult(pointsList)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
                onResult(emptyList())
            }
    }

    // Отримати точку маршруту за ID
    fun getRoutePoint(id: String, onResult: (Point?) -> Unit) {
        Log.d(TAG, "getRoutePoint")
        collection.document(id).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val point = document.toObject(Point::class.java)?.copy(id = document.id)
                    onResult(point)
                } else {
                    Log.d(TAG, "No such document")
                    onResult(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting document.", exception)
                onResult(null)
            }
    }

    // Оновити точку маршруту
    fun updateRoutePoint(id: String, name: String, city: String, address: String, client: String, description: String) {
        Log.d(TAG, "updateRoutePoint")
        val point = hashMapOf(
            "name" to name,
            "city" to city,
            "address" to address,
            "client" to client,
            "description" to description
        )
        collection.document(id).set(point)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
            }
    }

    // Видалити точку маршруту
    fun deleteRoutePoint(id: String) {
        Log.d(TAG, "deleteRoutePoint")
        collection.document(id).delete()
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting document", e)
            }
    }

    data class Point(
        val id: String = "",
        val name: String = "",
        val city: String = "",
        val address: String = "",
        val client: String = "",
        val description: String = ""
    )
    {
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
