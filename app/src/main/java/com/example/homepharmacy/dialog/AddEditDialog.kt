package com.example.homepharmacy

import android.app.AlertDialog
import android.content.Context
import android.widget.CheckBox
import android.widget.Toast
import android.view.LayoutInflater
import com.google.android.material.textfield.TextInputEditText

class AddEditDialog(
    private val context: Context,
    private val dao: MedicineDao,
    private val onSaveComplete: () -> Unit
) {

    fun show(medicine: Medicine?) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit, null)

        // Находим все поля
        val editName = dialogView.findViewById<TextInputEditText>(R.id.editName)
        val editExpiryDate = dialogView.findViewById<TextInputEditText>(R.id.editExpiryDate)
        val checkStatus = dialogView.findViewById<CheckBox>(R.id.checkStatus)
        val editInstructions = dialogView.findViewById<TextInputEditText>(R.id.editInstructions)
        val editPurchaseLocation = dialogView.findViewById<TextInputEditText>(R.id.editPurchaseLocation)
        val editPurchaseDate = dialogView.findViewById<TextInputEditText>(R.id.editPurchaseDate)

        val editPrice = dialogView.findViewById<TextInputEditText>(R.id.editPrice)


        // Если редактируем существующее лекарство - заполняем поля
        if (medicine != null) {
            editName.setText(medicine.name)
            editExpiryDate.setText(medicine.expiryDate)
            checkStatus.isChecked = medicine.status
            editInstructions.setText(medicine.instructions)
            editPurchaseLocation.setText(medicine.purchaseLocation)
            editPurchaseDate.setText(medicine.purchaseDate)
            editPrice.setText(medicine.price)
        }

        val dialog = AlertDialog.Builder(context)
            .setTitle(if (medicine == null) "Добавить лекарство" else "Редактировать лекарство")
            .setView(dialogView)
            .setPositiveButton("Сохранить", null)
            .setNegativeButton("Отмена", null)
            .create()

        dialog.show()

        // Переопределяем кнопку "Сохранить"
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val name = editName.text.toString().trim()
            val expiryDate = editExpiryDate.text.toString().trim()
            val status = checkStatus.isChecked
            val instructions = editInstructions.text.toString().trim()
            val purchaseLocation = editPurchaseLocation.text.toString().trim()
            val purchaseDate = editPurchaseDate.text.toString().trim()
            val price = editPrice.text.toString().trim()


            // Проверка на пустые поля
            when {
                name.isEmpty() -> {
                    editName.error = "Введите название лекарства"
                    return@setOnClickListener
                }

                expiryDate.isEmpty() -> {
                    editExpiryDate.error = "Введите срок годности"
                    return@setOnClickListener
                }

                // Проверка даты покупки (если поле не пустое)
                purchaseDate.isNotEmpty() && !isValidDate(purchaseDate) -> {
                    editPurchaseDate.error = "Неверный формат даты (ГГГГ-ММ-ДД)"
                    return@setOnClickListener
                }

                !isValidDate(expiryDate) -> {
                    editExpiryDate.error = "Неверный формат даты (ГГГГ-ММ-ДД)"
                    return@setOnClickListener
                }
            }

            try {
                if (medicine == null) {
                    // Добавление нового
                    val newMedicine = Medicine(
                        name = name,
                        expiryDate = expiryDate,
                        status = status,
                        instructions = instructions,
                        purchaseDate = purchaseDate,
                        purchaseLocation = purchaseLocation,
                        price = price
                    )
                    val result = dao.insert(newMedicine)
                    if (result != -1L) {
                        Toast.makeText(context, "Лекарство добавлено", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        onSaveComplete()
                    } else {
                        Toast.makeText(context, "Ошибка при добавлении", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Обновление существующего
                    val updatedMedicine = medicine.copy(
                        name = name,
                        expiryDate = expiryDate,
                        status = status,
                        instructions = instructions,
                        purchaseDate = purchaseDate,
                        purchaseLocation = purchaseLocation,
                        price = price
                    )
                    val result = dao.update(updatedMedicine)
                    if (result > 0) {
                        Toast.makeText(context, "Лекарство обновлено", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        onSaveComplete()
                    } else {
                        Toast.makeText(context, "Ошибка при обновлении", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    private fun isValidDate(date: String): Boolean {
        val regex = Regex("""^\d{4}-\d{2}-\d{2}$""")
        return regex.matches(date)
    }
}