package com.alsamei.soft.ui

import android.app.Activity
import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import com.alsamei.soft.R
import com.alsamei.soft.database.DatabaseHelper

class InventoryActivity : Activity() {

    private lateinit var adapter: ArrayAdapter<String>
    private val itemsList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        val etSearch = findViewById<EditText>(R.id.etSearchItem)
        val etName = findViewById<EditText>(R.id.etItemName)
        val etQty = findViewById<EditText>(R.id.etItemQty)
        val etPrice = findViewById<EditText>(R.id.etItemPrice)
        val btnAdd = findViewById<Button>(R.id.btnAddItem)
        val listView = findViewById<ListView>(R.id.lvItems)

        val dbHelper = DatabaseHelper(this)

        // دالة تحديث القائمة
        fun loadData() {
            itemsList.clear()
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT item_name, qty, price FROM items", null)
            while (cursor.moveToNext()) {
                itemsList.add("${cursor.getString(0)} | كمية: ${cursor.getDouble(1)} | سعر: ${cursor.getDouble(2)}")
            }
            cursor.close()
            adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemsList)
            listView.adapter = adapter
        }

        loadData()

        // برمجة محرك البحث
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }
        })

        // برمجة إضافة صنف جديد
        btnAdd.setOnClickListener {
            val name = etName.text.toString()
            val qty = etQty.text.toString().toDoubleOrNull() ?: 0.0
            val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0

            if (name.isNotEmpty()) {
                val db = dbHelper.writableDatabase
                val values = ContentValues().apply {
                    put("item_name", name)
                    put("qty", qty)
                    put("price", price)
                }
                db.insert("items", null, values)
                Toast.makeText(this, "تمت الإضافة بنجاح", Toast.LENGTH_SHORT).show()
                etName.text.clear()
                etQty.text.clear()
                etPrice.text.clear()
                loadData()
            }
        }
    }
}
