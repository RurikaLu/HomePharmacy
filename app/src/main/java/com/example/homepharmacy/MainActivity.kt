package com.example.homepharmacy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MedicineAdapter
    private lateinit var dao: MedicineDao
    private var allMedicines = listOf<Medicine>()

    // НОВЫЙ ЛАУНЧЕР ДЛЯ НАСТРОЕК
    private val settingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            android.util.Log.d("MainActivity", "Возврат из настроек, обновляем список")
            allMedicines = getSortedAndFilteredMedicines()
            adapter.updateList(allMedicines)

            val searchView = findViewById<androidx.appcompat.widget.SearchView>(R.id.searchView)
            filter(searchView.query.toString())

            Toast.makeText(this, "Список обновлён", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        try {
            // 1. Инициализация RecyclerView
            recyclerView = findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)

            // 2. Инициализация базы данных
            val dbHelper = MedicineDbHelper(this)
            dao = MedicineDao(dbHelper)

            // 3. Загрузка данных
            loadData()

            // 4. Настройка поиска
            val searchView = findViewById<androidx.appcompat.widget.SearchView>(R.id.searchView)
            searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean = false

                override fun onQueryTextChange(newText: String): Boolean {
                    filter(newText)
                    return true
                }
            })

            // 5. Кнопка настроек - ИСПРАВЛЕНО
            val btnSettings = findViewById<FloatingActionButton>(R.id.btnSettings)
            btnSettings.setOnClickListener {
                val intent = Intent(this, SettingsActivity::class.java)
                settingsLauncher.launch(intent)
            }

            // 6. Кнопка списка цен
            val btnPriceList = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnPriceList)
            btnPriceList.setOnClickListener {
                val intent = Intent(this, PriceListActivity::class.java)
                startActivity(intent)
            }

            // 7. Кнопка добавления
            val fab = findViewById<FloatingActionButton>(R.id.fabAdd)
            fab.setOnClickListener {
                showAddDialog()
            }

            Toast.makeText(this, "Приложение запущено", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun loadData() {
        try {
            // ИСПРАВЛЕНО: используем сортировку и фильтрацию
            allMedicines = getSortedAndFilteredMedicines()
            adapter = MedicineAdapter(allMedicines) { medicine ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("medicine_id", medicine.id)
                startActivity(intent)
            }
            recyclerView.adapter = adapter

            if (allMedicines.isEmpty()) {
                Toast.makeText(this, "Список лекарств пуст. Добавьте новое!", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка загрузки: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun filter(query: String) {
        try {
            if (query.isEmpty()) {
                adapter.updateList(allMedicines)
            } else {
                val filtered = dao.search(query)
                adapter.updateList(filtered)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка поиска: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddDialog() {
        try {
            val dialog = AddEditDialog(this, dao) {
                try {
                    // Обновляем список с учётом сортировки и фильтра
                    allMedicines = getSortedAndFilteredMedicines()
                    adapter.updateList(allMedicines)

                    val searchView = findViewById<androidx.appcompat.widget.SearchView>(R.id.searchView)
                    val query = searchView.query.toString()
                    if (query.isEmpty()) {
                        adapter.updateList(allMedicines)
                    } else {
                        val filtered = dao.search(query)
                        adapter.updateList(filtered)
                    }

                    Toast.makeText(this, "Список обновлен", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Ошибка обновления: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show(null)
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Сортировка и фильтрация

    // Сортировка и фильтрация
    private fun getSortedAndFilteredMedicines(): List<Medicine> {
        var medicines = dao.getAll()
        medicines = applyFilter(medicines)
        medicines = applySorting(medicines)
        return medicines
    }

    private fun applyFilter(medicines: List<Medicine>): List<Medicine> {
        val sharedPref = getSharedPreferences(SettingsActivity.PREF_NAME, MODE_PRIVATE)
        val filterType = sharedPref.getInt(SettingsActivity.KEY_FILTER_TYPE, SettingsActivity.FILTER_ALL)

        return when (filterType) {
            SettingsActivity.FILTER_AVAILABLE -> medicines.filter { it.status }
            SettingsActivity.FILTER_OUT_OF_STOCK -> medicines.filter { !it.status }
            else -> medicines
        }
    }

    private fun applySorting(medicines: List<Medicine>): List<Medicine> {
        val sharedPref = getSharedPreferences(SettingsActivity.PREF_NAME, MODE_PRIVATE)
        val sortType = sharedPref.getInt(SettingsActivity.KEY_SORT_TYPE, SettingsActivity.SORT_ALPHABETICAL_ASC)

        // Для сортировки без группировки (алфавит и дата добавления) - все лекарства вместе
        if (sortType == SettingsActivity.SORT_ALPHABETICAL_ASC ||
            sortType == SettingsActivity.SORT_ALPHABETICAL_DESC ||
            sortType == SettingsActivity.SORT_DATE_ASC ||
            sortType == SettingsActivity.SORT_DATE_DESC) {
            return sortGroup(medicines, sortType)  // сортируем всё вместе
        }

        // Для сортировки по сроку годности - сначала "Есть", потом "Нужно купить"
        val available = medicines.filter { it.status }
        val outOfStock = medicines.filter { !it.status }

        val sortedAvailable = sortGroup(available, sortType)
        val sortedOutOfStock = sortGroup(outOfStock, sortType)

        return sortedAvailable + sortedOutOfStock
    }

    private fun sortGroup(group: List<Medicine>, sortType: Int): List<Medicine> {
        return when (sortType) {
            SettingsActivity.SORT_ALPHABETICAL_ASC -> group.sortedWith(compareBy { it.name.lowercase() })
            SettingsActivity.SORT_ALPHABETICAL_DESC -> group.sortedWith(compareByDescending { it.name.lowercase() })
            SettingsActivity.SORT_EXPIRY_ASC -> group.sortedWith(compareBy { it.expiryDate })
            SettingsActivity.SORT_EXPIRY_DESC -> group.sortedWith(compareByDescending { it.expiryDate })
            SettingsActivity.SORT_DATE_ASC -> group.sortedWith(compareBy { it.id })
            SettingsActivity.SORT_DATE_DESC -> group.sortedWith(compareByDescending { it.id })

            else -> group
        }
    }




    override fun onResume() {
        super.onResume()
        try {
            allMedicines = getSortedAndFilteredMedicines()
            adapter.updateList(allMedicines)

            val searchView = findViewById<androidx.appcompat.widget.SearchView>(R.id.searchView)
            filter(searchView.query.toString())
        } catch (e: Exception) {
            // Игнорируем
        }
    }
}