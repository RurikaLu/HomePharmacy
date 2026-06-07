package com.example.homepharmacy

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog

class SettingsActivity : AppCompatActivity() {

    private lateinit var sortRadioGroup: RadioGroup
    private lateinit var filterRadioGroup: RadioGroup

    companion object {
        const val PREF_NAME = "app_settings"
        const val KEY_SORT_TYPE = "sort_type"
        const val KEY_FILTER_TYPE = "filter_type"

        const val SORT_ALPHABETICAL_ASC = 1
        const val SORT_ALPHABETICAL_DESC = 2
        const val SORT_EXPIRY_ASC = 3
        const val SORT_EXPIRY_DESC = 4
        const val SORT_DATE_ASC = 5
        const val SORT_DATE_DESC = 6

        const val FILTER_ALL = 1
        const val FILTER_AVAILABLE = 2
        const val FILTER_OUT_OF_STOCK = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // ПОКАЗЫВАЕМ СТРЕЛКУ "НАЗАД"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sortRadioGroup = findViewById(R.id.sortRadioGroup)
        filterRadioGroup = findViewById(R.id.filterRadioGroup)

        loadSavedSettings()

        sortRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            saveSorting(checkedId)
        }

        filterRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            saveFilter(checkedId)
        }

        // Кнопка "О приложении"
        findViewById<Button>(R.id.btnAbout).setOnClickListener {
            Toast.makeText(this, "ⓘ Домашняя аптечка v1.0\n\nРазработчик: Решетова Ольга\n2026 г", Toast.LENGTH_LONG).show()
        }

        // Кнопка "Очистить все данные"
        val btnClearData = findViewById<Button>(R.id.btnClearData)
        btnClearData.setOnClickListener {
            showClearDataDialog()
        }

        // КНОПКА "НАЗАД" (серая круглая внизу)
        val btnBack = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()  // Возвращаемся на главный экран
        }

    }

    // ОБРАБОТКА НАЖАТИЯ НА СТРЕЛКУ "НАЗАД"
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }


    private fun showClearDataDialog() {
        AlertDialog.Builder(this)
            .setTitle("Очистка данных")
            .setMessage("Вы уверены, что хотите удалить ВСЕ лекарства? Это действие нельзя отменить.")
            .setPositiveButton("Удалить") { _, _ ->
                clearAllData()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun clearAllData() {
        try {
            val dbHelper = MedicineDbHelper(this)
            val dao = MedicineDao(dbHelper)

            // Получаем все лекарства и удаляем каждое
            val medicines = dao.getAll()
            var deletedCount = 0
            medicines.forEach {
                dao.delete(it.id)
                deletedCount++
            }

            Toast.makeText(this, "Удалено $deletedCount лекарств", Toast.LENGTH_SHORT).show()

            // Закрываем настройки и возвращаемся на главный экран
            finish()

        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadSavedSettings() {
        val sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val savedSort = sharedPref.getInt(KEY_SORT_TYPE, SORT_ALPHABETICAL_ASC)
        when (savedSort) {
            SORT_ALPHABETICAL_ASC -> sortRadioGroup.check(R.id.radioSortAlphabeticalAsc)
            SORT_ALPHABETICAL_DESC -> sortRadioGroup.check(R.id.radioSortAlphabeticalDesc)
            SORT_EXPIRY_ASC -> sortRadioGroup.check(R.id.radioSortExpiryAsc)
            SORT_EXPIRY_DESC -> sortRadioGroup.check(R.id.radioSortExpiryDesc)
            SORT_DATE_ASC -> sortRadioGroup.check(R.id.radioSortDateAsc)
            SORT_DATE_DESC -> sortRadioGroup.check(R.id.radioSortDateDesc)
        }

        val savedFilter = sharedPref.getInt(KEY_FILTER_TYPE, FILTER_ALL)
        when (savedFilter) {
            FILTER_ALL -> filterRadioGroup.check(R.id.radioFilterAll)
            FILTER_AVAILABLE -> filterRadioGroup.check(R.id.radioFilterAvailable)
            FILTER_OUT_OF_STOCK -> filterRadioGroup.check(R.id.radioFilterOutOfStock)
        }
    }

    private fun saveSorting(checkedId: Int) {
        val sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val sortType = when (checkedId) {
            R.id.radioSortAlphabeticalAsc -> SORT_ALPHABETICAL_ASC
            R.id.radioSortAlphabeticalDesc -> SORT_ALPHABETICAL_DESC
            R.id.radioSortExpiryAsc -> SORT_EXPIRY_ASC
            R.id.radioSortExpiryDesc -> SORT_EXPIRY_DESC
            R.id.radioSortDateAsc -> SORT_DATE_ASC
            R.id.radioSortDateDesc -> SORT_DATE_DESC
            else -> SORT_ALPHABETICAL_ASC
        }
        sharedPref.edit().putInt(KEY_SORT_TYPE, sortType).apply()
        Toast.makeText(this, "Сортировка сохранена", Toast.LENGTH_SHORT).show()
    }

    private fun saveFilter(checkedId: Int) {
        val sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val filterType = when (checkedId) {
            R.id.radioFilterAll -> FILTER_ALL
            R.id.radioFilterAvailable -> FILTER_AVAILABLE
            R.id.radioFilterOutOfStock -> FILTER_OUT_OF_STOCK
            else -> FILTER_ALL
        }
        sharedPref.edit().putInt(KEY_FILTER_TYPE, filterType).apply()
        Toast.makeText(this, "Фильтр сохранён", Toast.LENGTH_SHORT).show()
    }

}