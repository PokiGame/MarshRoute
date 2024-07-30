package com.example.marshroute

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class DatabaseManager(context: Context) {
    private val dbHelper: DatabaseHelper = DatabaseHelper(context)
    companion object {
        private const val DATABASE_NAME = "route.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_ROUTE_POINTS = "route_points"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_DESCRIPTION = "description"
    }



    fun addRoutePoint(name: String, address: String, description: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("address", address)
            put("description", description)
        }
        db.insert("points", null, values)
    }

    fun getAllRoutePoints(): List<Point> {
        val points = mutableListOf<Point>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query("points", null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndexOrThrow("id")
            val nameIndex = cursor.getColumnIndexOrThrow("name")
            val addressIndex = cursor.getColumnIndexOrThrow("address")
            val descriptionIndex = cursor.getColumnIndexOrThrow("description")

            do {
                val id = cursor.getLong(idIndex)
                val name = cursor.getString(nameIndex)
                val address = cursor.getString(addressIndex)
                val description = cursor.getString(descriptionIndex)
                points.add(Point(id, name, address, description))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return points
    }



    fun getRoutePointById(id: Long): Point? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            "points",
            null,
            "id = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var point: Point? = null
        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val address = cursor.getString(cursor.getColumnIndexOrThrow("address"))
            val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
            point = Point(id, name, address, description)
        }
        cursor.close()
        return point
    }

    fun deleteRoutePoint(id: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(TABLE_ROUTE_POINTS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun updateRoutePoint(id: Long, name: String, address: String, description: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("address", address)
            put("description", description)
        }
        db.update("points", values, "id = ?", arrayOf(id.toString()))
    }
}
