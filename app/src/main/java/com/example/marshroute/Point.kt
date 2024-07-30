package com.example.marshroute

data class Point(
    val id: Long,
    val name: String,
    val address: String,
    val description: String
) {
    override fun toString(): String {
        return name // Або будь-яке інше представлення, яке ви хочете відобразити
    }
}
