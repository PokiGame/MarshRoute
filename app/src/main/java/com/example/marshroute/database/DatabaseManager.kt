package com.example.marshroute

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class DatabaseManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val context: Context = context
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "RoutePoints.db"
        private const val TABLE_ROUTE_POINTS = "RoutePoints"

        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_CITY = "city"
        private const val COLUMN_ADDRESS = "address"
        private const val COLUMN_CLIENT = "client"
        private const val COLUMN_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_ROUTE_POINTS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NAME TEXT, "
                + "$COLUMN_CITY TEXT, "
                + "$COLUMN_ADDRESS TEXT, "
                + "$COLUMN_CLIENT TEXT, "
                + "$COLUMN_DESCRIPTION TEXT)")
        db.execSQL(createTable)
    }



    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ROUTE_POINTS")
        onCreate(db)
    }
    fun getDatabasePath(): String {
        return context.getDatabasePath(DATABASE_NAME).absolutePath

    }
    fun addRoutePoint(name: String, city: String, address: String, client: String, description: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_CITY, city)
        values.put(COLUMN_ADDRESS, address)
        values.put(COLUMN_CLIENT, client)
        values.put(COLUMN_DESCRIPTION, description)
        return db.insert(TABLE_ROUTE_POINTS, null, values)
    }



    fun getAllRoutePoints(): List<Point> {
        val pointsList = mutableListOf<Point>()
        val selectQuery = "SELECT * FROM $TABLE_ROUTE_POINTS"
        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }

        cursor.use {
            while (it.moveToNext()) {
                val idIndex = it.getColumnIndex(COLUMN_ID)
                val nameIndex = it.getColumnIndex(COLUMN_NAME)
                val cityIndex = it.getColumnIndex(COLUMN_CITY)
                val addressIndex = it.getColumnIndex(COLUMN_ADDRESS)
                val clientIndex = it.getColumnIndex(COLUMN_CLIENT)
                val descriptionIndex = it.getColumnIndex(COLUMN_DESCRIPTION)

                if (idIndex != -1 && nameIndex != -1 && cityIndex != -1 && addressIndex != -1 && clientIndex != -1 && descriptionIndex != -1) {
                    val id = it.getLong(idIndex)
                    val name = it.getString(nameIndex)
                    val city = it.getString(cityIndex)
                    val address = it.getString(addressIndex)
                    val client = it.getString(clientIndex)
                    val description = it.getString(descriptionIndex)

                    pointsList.add(Point(id, name, city, address, client, description))
                } else {
                    Log.e("DatabaseManager", "One or more column indices are invalid")
                }
            }
        }
        return pointsList
    }

    fun getRoutePoint(id: Long): Point? {
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_ROUTE_POINTS WHERE $COLUMN_ID = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(id.toString()))

        var point: Point? = null

        cursor.use {
            if (it.moveToFirst()) {
                val idIndex = it.getColumnIndex(COLUMN_ID)
                val nameIndex = it.getColumnIndex(COLUMN_NAME)
                val cityIndex = it.getColumnIndex(COLUMN_CITY)
                val addressIndex = it.getColumnIndex(COLUMN_ADDRESS)
                val clientIndex = it.getColumnIndex(COLUMN_CLIENT)
                val descriptionIndex = it.getColumnIndex(COLUMN_DESCRIPTION)

                if (idIndex != -1 && nameIndex != -1 && cityIndex != -1 && addressIndex != -1 && clientIndex != -1 && descriptionIndex != -1) {
                    val pointId = it.getLong(idIndex)
                    val name = it.getString(nameIndex)
                    val city = it.getString(cityIndex)
                    val address = it.getString(addressIndex)
                    val client = it.getString(clientIndex)
                    val description = it.getString(descriptionIndex)

                    point = Point(pointId, name, city, address, client, description)
                } else {
                    Log.e("DatabaseError", "Column index not found.")
                }
            }
        }
        return point
    }

    fun updateRoutePoint(id: Long, city: String, name: String, address: String, client: String, description: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_CITY, city)
            put(COLUMN_ADDRESS, address)
            put(COLUMN_CLIENT, client)
            put(COLUMN_DESCRIPTION, description)
        }
        return db.update(TABLE_ROUTE_POINTS, values, "$COLUMN_ID=?", arrayOf(id.toString()))
    }

    fun deleteRoutePoint(id: Long): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_ROUTE_POINTS, "$COLUMN_ID=?", arrayOf(id.toString()))
    }

    data class Point(
        val id: Long,
        val name: String,
        val city: String,
        val address: String,
        val client: String,
        val description: String
    ) {
        override fun toString(): String {
            return "$name\n$id"
        }
    }
}
