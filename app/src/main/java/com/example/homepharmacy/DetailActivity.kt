package com.example.homepharmacy

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton


class DetailActivity : AppCompatActivity() {

    private lateinit var dao: MedicineDao
    private var medicineId: Int = -1
    private var currentMedicine: Medicine? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Инициализация БД
        val dbHelper = MedicineDbHelper(this)
        dao = MedicineDao(dbHelper)

        // Получаем ID лекарства из Intent
        medicineId = intent.getIntExtra("medicine_id", -1)

        // Загружаем лекарство из БД
        currentMedicine = dao.getAll().find { it.id == medicineId }

        if (currentMedicine == null) {
            finish()
            return
        }

        // Отображаем данные
        displayMedicineInfo(currentMedicine!!)

        // Кнопка редактирования
        findViewById<Button>(R.id.btnEdit).setOnClickListener {
            showEditDialog()
        }

        // Кнопка удаления
        findViewById<Button>(R.id.btnDelete).setOnClickListener {
            showDeleteConfirmationDialog()
        }

        val btnBack = findViewById<FloatingActionButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()  // Возвращаемся на главный экран
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()  // Закрываем текущую активность и возвращаемся назад
        return true
    }

    private fun displayMedicineInfo(medicine: Medicine) {
        findViewById<TextView>(R.id.detailName).text = medicine.name
        findViewById<TextView>(R.id.detailPeriod).text = "Годен до: ${medicine.expiryDate}"

        val statusText = if (medicine.status) {
            "Есть"
        } else {
            "Нужно купить"
        }
        findViewById<TextView>(R.id.detailStatus).text = statusText

        findViewById<TextView>(R.id.detailInstructions).text = medicine.instructions

        // Дата покупки
        val purchaseDateText = if (medicine.purchaseDate.isNotEmpty()) medicine.purchaseDate else "Не указана"
        findViewById<TextView>(R.id.detailPurchaseDate).text = purchaseDateText

        // Цена
        val priceText = if (medicine.price.isNotEmpty()) "${medicine.price} ₽" else "Не указана"
        findViewById<TextView>(R.id.detailPrice).text = priceText

        // Где купили
        val locationText = if (medicine.purchaseLocation.isNotEmpty()) medicine.purchaseLocation else "Не указано"
        findViewById<TextView>(R.id.detailPurchaseLocation).text = locationText

    }

    private fun showEditDialog() {
        val dialog = AddEditDialog(this, dao) {
            currentMedicine = dao.getAll().find { it.id == medicineId }
            if (currentMedicine != null) {
                displayMedicineInfo(currentMedicine!!)
            }
        }
        dialog.show(currentMedicine)
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Удаление")
            .setMessage("Вы уверены, что хотите удалить лекарство \"${currentMedicine?.name}\"?")
            .setPositiveButton("Удалить") { _, _ ->
                dao.delete(medicineId)
                Toast.makeText(this, "Лекарство удалено", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}