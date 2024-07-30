package com.example.marshroute

import android.util.Log

data class Point(
    val id: Long,
    val name: String,
    val city: String,
    val address: String,
    val client: String,
    val description: String

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
