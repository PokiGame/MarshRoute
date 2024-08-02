package com.example.marshroute

import android.util.Log

data class Point(
    val id: String,
    val name: String,
    val city: String,
    val address: String,
    val client: String,
    val description: String,
    val coordinates: String

) {
    constructor() : this("", "", "", "", "", "", "")
    override fun toString(): String {
        var clienttemp: String = client
        var descriptiontemp: String = description
        var addresstemp: String = address
        if (client.isNotEmpty()){ clienttemp = ", " + client }
        if (description.isNotEmpty()){ descriptiontemp = ", " + description }
        if (address.isNotEmpty()){ addresstemp = ", " + address }
        Log.d("EditPointDetailsActivity", client.toString())
        Log.d("EditPointDetailsActivity", description.toString())
        return "$name, $city, $coordinates$addresstemp$clienttemp$descriptiontemp"
    }
}
