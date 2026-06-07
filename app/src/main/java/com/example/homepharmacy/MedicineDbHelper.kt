package com.example.homepharmacy

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MedicineDbHelper(context: Context) : SQLiteOpenHelper(context, "medicines.db", null, 6) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(MedicineContract.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // При любом обновлении удаляем старую таблицу и создаём новую
        db.execSQL("DROP TABLE IF EXISTS ${MedicineContract.TABLE_NAME}")
        onCreate(db)
    }
}