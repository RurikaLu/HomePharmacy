package com.example.homepharmacy

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PriceListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PriceListAdapter
    private lateinit var dao: MedicineDao
    private var allMedicines = listOf<Medicine>()
    private var currentSortType = SORT_ALPHABETICAL_ASC
    private var currentFilter = FILTER_ALL

    companion object {
        const val SORT_ALPHABETICAL_ASC = 1
        const val SORT_ALPHABETICAL_DESC = 2
        const val SORT_PRICE_ASC = 3
        const val SORT_PRICE_DESC = 4
        const val SORT_DATE_ADDED_ASC = 5      // старые сверху
        const val SORT_DATE_ADDED_DESC = 6     // новые сверху
        const val SORT_PURCHASE_DATE_ASC = 7   // старые покупки сверху
        const val SORT_PURCHASE_DATE_DESC = 8  // новые покупки сверху

        const val FILTER_ALL = 1
        const val FILTER_NO_PRICE = 2
        const val FILTER_ONLY_PRICE = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_price_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dbHelper = MedicineDbHelper(this)
        dao = MedicineDao(dbHelper)

        // Поиск
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean = false
            override fun onQueryTextChange(newText: String): Boolean {
                filterBySearch(newText)
                return true
            }
        })

        // Кнопка с тремя точками (открывает меню)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        btnMenu.setOnClickListener {
            showSortMenu()
        }

        // Кнопка назад
        val btnBack = findViewById<FloatingActionButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        loadData()
    }

    // Меню с сортировками и фильтрами (через AlertDialog)
    private fun showSortMenu() {
        val options = arrayOf(
            "━━━━━━━━━━━━━━━━━━",
            "・Показать все",
            "・Показать только с ценой",
            "・Показать только без цены",
            "━━━━━━━━━━━━━━━━━━",
            "・По алфавиту:  А → Я",
            "・По алфавиту:  Я → А",
            "・По цене:  дороже ⬇",
            "・По цене:  дороже ⬆",
            "・По дате добавления:  новые ⬆ ",
            "・По дате добавления:  новые ⬇",
            "・По дате покупки:  новые ⬆",
            "・По дате покупки:  новые ⬇",
            )

        AlertDialog.Builder(this)
            .setTitle("Сортировка и фильтры")
            .setItems(options) { _, which ->
                when (which) {
                    1 -> {
                        currentFilter = FILTER_ALL
                        loadData()
                        Toast.makeText(this, "Показать все", Toast.LENGTH_SHORT).show()
                    }
                    2 -> {
                        currentFilter = FILTER_ONLY_PRICE
                        loadData()
                        Toast.makeText(this, "Фильтр: только с ценой", Toast.LENGTH_SHORT).show()
                    }
                    3 -> {
                        currentFilter = FILTER_NO_PRICE
                        loadData()
                        Toast.makeText(this, "Фильтр: только без цены", Toast.LENGTH_SHORT).show()
                    }

                    5 -> {
                        currentSortType = SORT_ALPHABETICAL_ASC
                        currentFilter = FILTER_ALL
                        loadData()
                        Toast.makeText(this, "Сортировка: по алфавиту (А→Я)", Toast.LENGTH_SHORT).show()
                    }
                    6 -> {
                        currentSortType = SORT_ALPHABETICAL_DESC
                        currentFilter = FILTER_ALL
                        loadData()
                        Toast.makeText(this, "Сортировка: по алфавиту (Я→А)", Toast.LENGTH_SHORT).show()
                    }
                    7 -> {
                        currentSortType = SORT_PRICE_ASC
                        currentFilter = FILTER_ALL
                        loadData()
                        Toast.makeText(this, "Сортировка: сначала дешёвые", Toast.LENGTH_SHORT).show()
                    }
                    8 -> {
                        currentSortType = SORT_PRICE_DESC
                        currentFilter = FILTER_ALL
                        loadData()
                        Toast.makeText(this, "Сортировка: сначала дорогие", Toast.LENGTH_SHORT).show()
                    }
                    9 -> {
                        currentSortType = SORT_DATE_ADDED_DESC
                        currentFilter = FILTER_ALL
                        loadData()
                        Toast.makeText(this, "Сортировка: по дате добавления (новые сверху)", Toast.LENGTH_SHORT).show()
                    }
                    10 -> {
                        currentSortType = SORT_DATE_ADDED_ASC
                        currentFilter = FILTER_ALL
                        loadData()
                        Toast.makeText(this, "Сортировка: по дате добавления (старые сверху)", Toast.LENGTH_SHORT).show()
                    }
                    11 -> {
                        currentSortType = SORT_PURCHASE_DATE_DESC
                        currentFilter = FILTER_ALL
                        loadData()
                        Toast.makeText(this, "Сортировка: по дате покупки (новые сверху)", Toast.LENGTH_SHORT).show()
                    }
                    12 -> {
                        currentSortType = SORT_PURCHASE_DATE_ASC
                        currentFilter = FILTER_ALL
                        loadData()
                        Toast.makeText(this, "Сортировка: по дате покупки (старые сверху)", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }

    private fun loadData() {
        var medicines = dao.getAll()

        medicines = when (currentFilter) {
            FILTER_NO_PRICE -> medicines.filter { it.price.isEmpty() }
            FILTER_ONLY_PRICE -> medicines.filter { it.price.isNotEmpty() }
            else -> medicines
        }

        medicines = applySorting(medicines)

        allMedicines = medicines
        adapter = PriceListAdapter(allMedicines) { medicine ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("medicine_id", medicine.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    private fun applySorting(medicines: List<Medicine>): List<Medicine> {
        val noPrice = medicines.filter { it.price.isEmpty() }
        val withPrice = medicines.filter { it.price.isNotEmpty() }

        return when (currentSortType) {
            // Алфавит (все вместе)
            SORT_ALPHABETICAL_ASC -> medicines.sortedWith(compareBy { it.name.lowercase() })
            SORT_ALPHABETICAL_DESC -> medicines.sortedWith(compareByDescending { it.name.lowercase() })

            // Цена (с ценой сначала, потом без цены)
            SORT_PRICE_ASC -> {
                val sortedWithPrice = withPrice.sortedWith(
                    compareBy<Medicine> { it.price.toDoubleOrNull() ?: Double.MAX_VALUE }
                        .thenBy { it.name.lowercase() }
                )
                val sortedNoPrice = noPrice.sortedWith(compareBy { it.name.lowercase() })
                sortedWithPrice + sortedNoPrice
            }
            SORT_PRICE_DESC -> {
                val sortedWithPrice = withPrice.sortedWith(
                    compareByDescending<Medicine> { it.price.toDoubleOrNull() ?: Double.MIN_VALUE }
                        .thenBy { it.name.lowercase() }
                )
                val sortedNoPrice = noPrice.sortedWith(compareBy { it.name.lowercase() })
                sortedWithPrice + sortedNoPrice
            }

            // Дата добавления (по ID) - все вместе
            SORT_DATE_ADDED_ASC -> medicines.sortedWith(compareBy { it.id })           // старые сверху
            SORT_DATE_ADDED_DESC -> medicines.sortedWith(compareByDescending { it.id }) // новые сверху

            // Дата покупки (все вместе, те у кого не указана — внизу)
            SORT_PURCHASE_DATE_ASC -> {
                val withPurchaseDate = medicines.filter { it.purchaseDate.isNotEmpty() }
                val withoutPurchaseDate = medicines.filter { it.purchaseDate.isEmpty() }
                val sortedWithDate = withPurchaseDate.sortedWith(compareBy { it.purchaseDate })
                sortedWithDate + withoutPurchaseDate
            }
            SORT_PURCHASE_DATE_DESC -> {
                val withPurchaseDate = medicines.filter { it.purchaseDate.isNotEmpty() }
                val withoutPurchaseDate = medicines.filter { it.purchaseDate.isEmpty() }
                val sortedWithDate = withPurchaseDate.sortedWith(compareByDescending { it.purchaseDate })
                sortedWithDate + withoutPurchaseDate
            }

            else -> medicines
        }
    }

    private fun filterBySearch(query: String) {
        if (query.isEmpty()) {
            loadData()
        } else {
            val filtered = allMedicines.filter { it.name.lowercase().contains(query.lowercase()) }
            adapter.updateList(filtered)
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}