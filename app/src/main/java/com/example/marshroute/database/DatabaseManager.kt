package com.example.marshroute

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class DatabaseManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "RoutePointsDB"
        private const val TABLE_ROUTE_POINTS = "RoutePoints"

        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_ADDRESS = "address"
        private const val COLUMN_DESCRIPTION = "description"
    }
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_ROUTE_POINTS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NAME TEXT, "
                + "$COLUMN_ADDRESS TEXT, "
                + "$COLUMN_DESCRIPTION TEXT)")
        db.execSQL(createTable)
    }



    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ROUTE_POINTS")
        onCreate(db)
    }


    fun addRoutePoint(name: String, address: String, description: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_ADDRESS, address)
        values.put(COLUMN_DESCRIPTION, description)
        return db.insert(TABLE_ROUTE_POINTS, null, values)
    }

    fun getAllRoutePoints(): List<Point> {
        val pointsList = ArrayList<Point>()
        val selectQuery = "SELECT * FROM $TABLE_ROUTE_POINTS"
        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }

        var id: Long
        var name: String
        var address: String
        var description: String

        if (cursor.moveToFirst()) {
            do {
                val idIndex = cursor.getColumnIndex(COLUMN_ID)
                val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
                val addressIndex = cursor.getColumnIndex(COLUMN_ADDRESS)
                val descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION)

                if (idIndex == -1 || nameIndex == -1 || addressIndex == -1 || descriptionIndex == -1) {
                    throw RuntimeException("Column index not found")
                }

                id = cursor.getLong(idIndex)
                name = cursor.getString(nameIndex)
                address = cursor.getString(addressIndex)
                description = cursor.getString(descriptionIndex)

                val point = Point(id, name, address, description)
                pointsList.add(point)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return pointsList
    }


    fun getRoutePoint(id: Long): Point? {
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_ROUTE_POINTS WHERE $COLUMN_ID = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(id.toString()))

        var point: Point? = null

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(COLUMN_ID)
            val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
            val addressIndex = cursor.getColumnIndex(COLUMN_ADDRESS)
            val descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION)

            if (idIndex != -1 && nameIndex != -1 && addressIndex != -1 && descriptionIndex != -1) {
                val pointId = cursor.getLong(idIndex)
                val name = cursor.getString(nameIndex)
                val address = cursor.getString(addressIndex)
                val description = cursor.getString(descriptionIndex)

                point = Point(pointId, name, address, description)
            } else {
                Log.e("DatabaseError", "Column index not found.")
            }
        }
        cursor.close()
        return point
    }


    fun updateRoutePoint(id: Long, name: String, address: String, description: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_ADDRESS, address)
        values.put(COLUMN_DESCRIPTION, description)

        return db.update(TABLE_ROUTE_POINTS, values, "$COLUMN_ID=?", arrayOf(id.toString()))
    }

    fun deleteRoutePoint(id: Long): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_ROUTE_POINTS, "$COLUMN_ID=?", arrayOf(id.toString()))
    }



    data class Point(val id: Long, val name: String, val address: String, val description: String) {
        override fun toString(): String {
            return name
        }
    }
}
