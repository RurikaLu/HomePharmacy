package com.example.homepharmacy

object MedicineContract {
    const val TABLE_NAME = "medicines"
    const val COL_ID = "_id"
    const val COL_NAME = "name"
    const val COL_EXPIRY = "expiry_date"

    const val COL_STATUS = "status"
    const val COL_INSTRUCTIONS = "instructions"
    const val COL_PURCHASE_LOCATION = "purchase_location"

    const val COL_PURCHASE_DATE = "purchase_date"
    const val COL_PRICE = "price"

    const val CREATE_TABLE = """
        CREATE TABLE $TABLE_NAME (
            $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_NAME TEXT NOT NULL,
            $COL_EXPIRY TEXT NOT NULL,
            $COL_STATUS INTEGER NOT NULL,
            $COL_INSTRUCTIONS TEXT NOT NULL,
            $COL_PURCHASE_LOCATION TEXT,
            $COL_PURCHASE_DATE TEXT,
            $COL_PRICE TEXT
        )
    """
}