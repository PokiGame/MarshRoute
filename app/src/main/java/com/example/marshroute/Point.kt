package com.example.marshroute

data class Point(
    val id: Long,
    val name: String,
    val city: String,
    val address: String,
    val client: String,
    val description: String

) {
    override fun toString(): String {
        return "$name, $city, $address, $client, $description"
    }
}
