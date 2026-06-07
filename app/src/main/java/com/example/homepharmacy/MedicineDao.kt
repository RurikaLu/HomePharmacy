package com.example.homepharmacy

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.Cursor

class MedicineDao(private val dbHelper: MedicineDbHelper) {

    // Получить все лекарства
    fun getAll(): List<Medicine> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            MedicineContract.TABLE_NAME,
            null, null, null, null, null, null
        )
        val medicines = mutableListOf<Medicine>()
        while (cursor.moveToNext()) {
            medicines.add(cursorToMedicine(cursor))
        }
        cursor.close()
        db.close()
        return medicines
    }

    // Поиск по названию или рекомендациям
    fun search(query: String): List<Medicine> {
        val db = dbHelper.readableDatabase
        val selection = "${MedicineContract.COL_NAME} LIKE ? OR ${MedicineContract.COL_INSTRUCTIONS} LIKE ?"
        val args = arrayOf("%$query%", "%$query%")
        val cursor = db.query(
            MedicineContract.TABLE_NAME,
            null,
            selection,
            args,
            null,
            null,
            null
        )
        val result = mutableListOf<Medicine>()
        while (cursor.moveToNext()) {
            result.add(cursorToMedicine(cursor))
        }
        cursor.close()
        db.close()
        return result
    }

    // Добавить новое лекарство
    fun insert(medicine: Medicine): Long {
        return try {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(MedicineContract.COL_NAME, medicine.name)
                put(MedicineContract.COL_EXPIRY, medicine.expiryDate)
                put(MedicineContract.COL_STATUS, if (medicine.status) 1 else 0)
                put(MedicineContract.COL_INSTRUCTIONS, medicine.instructions)
                put(MedicineContract.COL_PURCHASE_LOCATION, medicine.purchaseLocation)
                put(MedicineContract.COL_PURCHASE_DATE, medicine.purchaseDate)
                put(MedicineContract.COL_PRICE, medicine.price)
            }
            val result = db.insert(MedicineContract.TABLE_NAME, null, values)
            db.close()
            result
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    // Обновить лекарство
    fun update(medicine: Medicine): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(MedicineContract.COL_NAME, medicine.name)
            put(MedicineContract.COL_EXPIRY, medicine.expiryDate)
            put(MedicineContract.COL_STATUS, if (medicine.status) 1 else 0)
            put(MedicineContract.COL_INSTRUCTIONS, medicine.instructions)
            put(MedicineContract.COL_PURCHASE_LOCATION, medicine.purchaseLocation)
            put(MedicineContract.COL_PURCHASE_DATE, medicine.purchaseDate)
            put(MedicineContract.COL_PRICE, medicine.price)
        }
        val result = db.update(
            MedicineContract.TABLE_NAME,
            values,
            "${MedicineContract.COL_ID}=?",
            arrayOf(medicine.id.toString())
        )
        db.close()
        return result
    }

    // Удалить лекарство
    fun delete(id: Int): Int {
        val db = dbHelper.writableDatabase
        val result = db.delete(
            MedicineContract.TABLE_NAME,
            "${MedicineContract.COL_ID}=?",
            arrayOf(id.toString())
        )
        db.close()
        return result
    }

    // Получить одно лекарство по ID
    fun getById(id: Int): Medicine? {
        val db = dbHelper.readableDatabase
        val selection = "${MedicineContract.COL_ID}=?"
        val args = arrayOf(id.toString())
        val cursor = db.query(
            MedicineContract.TABLE_NAME,
            null,
            selection,
            args,
            null,
            null,
            null
        )
        var medicine: Medicine? = null
        if (cursor.moveToFirst()) {
            medicine = cursorToMedicine(cursor)
        }
        cursor.close()
        db.close()
        return medicine
    }

    // Преобразовать курсор в объект Medicine
    private fun cursorToMedicine(cursor: Cursor): Medicine {
        return Medicine(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(MedicineContract.COL_ID)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(MedicineContract.COL_NAME)),
            expiryDate = cursor.getString(cursor.getColumnIndexOrThrow(MedicineContract.COL_EXPIRY)),
            status = cursor.getInt(cursor.getColumnIndexOrThrow(MedicineContract.COL_STATUS)) == 1,
            instructions = cursor.getString(cursor.getColumnIndexOrThrow(MedicineContract.COL_INSTRUCTIONS)),
            purchaseLocation = cursor.getString(cursor.getColumnIndexOrThrow(MedicineContract.COL_PURCHASE_LOCATION)) ?: "",
            purchaseDate = cursor.getString(cursor.getColumnIndexOrThrow(MedicineContract.COL_PURCHASE_DATE)) ?: "",
            price = cursor.getString(cursor.getColumnIndexOrThrow(MedicineContract.COL_PRICE)) ?: ""
        )
    }
}